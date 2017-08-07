package com.example.ihortovpinets.myaccounts;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainUnitTests {

	private static final double START_AMOUNT1 = 1000;
	private static final double DELTA_AMOUNT1_LESS = 111;
	private static final double DELTA_AMOUNT1_MORE = 11111;

	private static final double START_AMOUNT2 = 1005;

	private Account firstAccount;
	private Account secondAccount;

	@Before
	public void init() {
		firstAccount = new Account("Account1", START_AMOUNT1, "");
		secondAccount = new Account("Account2", START_AMOUNT2, "");
	}

	@Test // testing deposit changing if increasing
	public void depositShouldBeIncreasedAnyCase() {
		assertTrue(firstAccount.performAccrual(DELTA_AMOUNT1_LESS));
		assertEquals("Accrual for Inner Account: if amount > delta", START_AMOUNT1 + DELTA_AMOUNT1_LESS, firstAccount.getDeposit(), 0);

		assertTrue(firstAccount.performAccrual(DELTA_AMOUNT1_MORE));
		assertEquals("Accrual for Inner Account: if amount < delta", START_AMOUNT1 + DELTA_AMOUNT1_LESS + DELTA_AMOUNT1_MORE, firstAccount.getDeposit(), 0);

		assertTrue(firstAccount.performAccrual(START_AMOUNT1 + DELTA_AMOUNT1_LESS + DELTA_AMOUNT1_MORE));
		assertEquals("Accrual for Inner Account: if amount = delta", (START_AMOUNT1 + DELTA_AMOUNT1_LESS + DELTA_AMOUNT1_MORE) * 2, firstAccount.getDeposit(), 0);
	}

	@Test // testing deposit changing if decreasing
	public void depositShouldBeDecreasedIfAmountMoreOrEqualThanChangeValue() {
		assertTrue(firstAccount.performWithdrawal(DELTA_AMOUNT1_LESS));
		assertEquals("Withdrawal for Inner Account: if amount > delta : should be successful with deposit changes",
				START_AMOUNT1 - DELTA_AMOUNT1_LESS, firstAccount.getDeposit(), 0);

		assertFalse(firstAccount.performWithdrawal(DELTA_AMOUNT1_MORE));
		assertEquals("Withdrawal for Inner Account: if amount < delta : should be failed without deposit changes",
				START_AMOUNT1 - DELTA_AMOUNT1_LESS, firstAccount.getDeposit(), 0);

		assertTrue(firstAccount.performWithdrawal(START_AMOUNT1 - DELTA_AMOUNT1_LESS));
		assertEquals("Withdrawal for Inner Account: if amount = delta : should be successful with deposit changes",
				0, firstAccount.getDeposit(), 0);

	}

	@Test
	public void depositChangingWhileDealCreation() {
		// firstAccount - buyer - the one who has deposit decreased
		// secondAccount - seller - the one who has deposit increased
	}

}