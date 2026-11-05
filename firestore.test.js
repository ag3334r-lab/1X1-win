const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_123";
const BOB_UID = "bob_uid_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function buildValidProfile(uid) {
  return {
    userId: uid,
    playerId: "8627577",
    displayName: "RoyalPlayer",
    avatarIndex: 1,
    mainBalance: 500.0,
    bonusBalance: 50.0,
    safeBalance: 0.0,
    vipLevel: 1,
    vipPoints: 10,
    streakDays: 1,
    lastSpinDay: 0,
    referralCode: "RGC8627",
    paymentMethod: "JAZZCASH",
    accountNumber: "03001234567",
    accountHolder: "Ali Khan",
    createdAt: new Date(Date.now() - 2000),
    updatedAt: new Date(Date.now() - 2000),
  };
}

test("Unauthenticated user cannot read or create profiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set(buildValidProfile(ALICE_UID))
  );
});

test("Authenticated user can create, read, and update their own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profile = buildValidProfile(ALICE_UID);
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(profile));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      mainBalance: 1200.0,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Authenticated user cannot read or modify another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx
      .firestore()
      .collection("users")
      .doc(BOB_UID)
      .set(buildValidProfile(BOB_UID));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({ mainBalance: 999999.0 })
  );
});

test("Shadow update with ghost field is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profile = buildValidProfile(ALICE_UID);
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(profile));
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isSuperAdmin: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Transactions subcollection enforces ownership and terminal status locking", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const txRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("transactions")
    .doc("tx_001");

  const validTx = {
    userId: ALICE_UID,
    type: "WITHDRAW",
    method: "JAZZCASH",
    amount: 500.0,
    status: "PENDING",
    referenceNote: "03001234567",
    createdAt: new Date(Date.now() - 2000),
    updatedAt: new Date(Date.now() - 2000),
  };

  await assertSucceeds(txRef.set(validTx));
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .where("userId", "==", ALICE_UID)
      .get()
  );

  // Approve pending transaction
  await assertSucceeds(
    txRef.update({
      status: "APPROVED",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Terminal state lock: once APPROVED, cannot be mutated again
  await assertFails(
    txRef.update({
      status: "PENDING",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Game history subcollection allows append-only creation and owner list query", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const roundRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("game_history")
    .doc("round_001");

  await assertSucceeds(
    roundRef.set({
      userId: ALICE_UID,
      gameId: "zoo_roulette",
      gameTitle: "Zoo Roulette",
      betAmount: 100.0,
      winAmount: 300.0,
      outcomeText: "Lion 3x Win!",
      createdAt: new Date(Date.now() - 1000),
    })
  );

  await assertFails(roundRef.update({ winAmount: 99999.0 }));
});
