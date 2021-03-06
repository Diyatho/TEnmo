package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransactionHistory;
import com.techelevator.tenmo.model.TransferFunds;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserSqlDAO implements UserDAO {

	private static final double STARTING_BALANCE = 1000;
	private JdbcTemplate jdbcTemplate;

	public UserSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int findIdByUsername(String username) {
		return jdbcTemplate.queryForObject("select user_id from users where username = ?", int.class, username);
	}

	@Override
	public List<User> findAll() {
		List<User> users = new ArrayList<>();
		String sql = "select * from users";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			User user = mapRowToUser(results);
			users.add(user);
		}

		return users;
	}

	@Override
	public User findByUsername(String username) throws UsernameNotFoundException {
		for (User user : this.findAll()) {
			if (user.getUsername().toLowerCase().equals(username.toLowerCase())) {
				return user;
			}
		}
		throw new UsernameNotFoundException("User " + username + " was not found.");
	}

	@Override
	public boolean create(String username, String password) {
		boolean userCreated = false;
		boolean accountCreated = false;

		// create user
		String insertUser = "insert into users (username,password_hash) values(?,?)";
		String password_hash = new BCryptPasswordEncoder().encode(password);

		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		String id_column = "user_id";
		userCreated = jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(insertUser, new String[] { id_column });
			ps.setString(1, username);
			ps.setString(2, password_hash);
			return ps;
		}, keyHolder) == 1;
		int newUserId = (int) keyHolder.getKeys().get(id_column);

		// create account
		String insertAccount = "insert into accounts (user_id,balance) values(?,?)";
		accountCreated = jdbcTemplate.update(insertAccount, newUserId, STARTING_BALANCE) == 1;

		return userCreated && accountCreated;
	}

	private User mapRowToUser(SqlRowSet rs) {
		User user = new User();
		user.setId(rs.getLong("user_id"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password_hash"));
		user.setActivated(true);
		user.setAuthorities("ROLE_USER");
		return user;
	}

	@Override
	public BigDecimal getBalance(int id) {
		BigDecimal balance = BigDecimal.ZERO;
		String queryForBalance = "SELECT balance FROM accounts WHERE user_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(queryForBalance, id);
		while (result.next()) {
			balance = result.getBigDecimal("balance");
		}
		return balance;
	}

	@Override
	public List<User> getUsers(int id) {
		List<User> users = new ArrayList<>();

		String queryForUsers = "SELECT user_id, username FROM users WHERE user_id != ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(queryForUsers, id);
		while (result.next()) {
			User user = mapRowToOtherUsers(result);
			users.add(user);
		}
		return users;
	}

	private User mapRowToOtherUsers(SqlRowSet rs) {
		User user = new User();
		user.setId(rs.getLong("user_id"));
		user.setUsername(rs.getString("username"));

		return user;
	}

	@Override
	public boolean transfer(TransferFunds transferFunds) {
		if (getBalance(transferFunds.getSenderId()).compareTo(transferFunds.getAmount()) > 0) {
			String sqlUpdateSenderBalance = "UPDATE accounts SET balance = ? WHERE user_id = ?";
			BigDecimal senderBalanceAfterTransfer = getBalance(transferFunds.getSenderId())
					.subtract(transferFunds.getAmount());
			jdbcTemplate.update(sqlUpdateSenderBalance, senderBalanceAfterTransfer, transferFunds.getSenderId());
			String sqlUpdateReceiverBalance = "UPDATE accounts SET balance = ? WHERE user_id = ?";
			BigDecimal receiverBalanceAfterTransfer = getBalance(transferFunds.getReceiverId())
					.add(transferFunds.getAmount());
			jdbcTemplate.update(sqlUpdateReceiverBalance, receiverBalanceAfterTransfer, transferFunds.getReceiverId());
			String sqlInsertIntoTransfer = "INSERT INTO transfers(transfer_type_id,transfer_status_id,account_from,account_to,amount)VALUES (?,?,?,?,?)";
			jdbcTemplate.update(sqlInsertIntoTransfer, 2, 2, transferFunds.getSenderId(), transferFunds.getReceiverId(),
					transferFunds.getAmount());
			return true;
		}
		return false;
	}

	@Override
	public List<TransactionHistory> getUserHistory(int id) {
		List<TransactionHistory> transactions = new ArrayList<>();
		String sqlTransactionDetails = "Select transfer_id, transfer_type_id, transfer_status_desc, amount,users.username as sender, userscopy.username as receiver from transfers\n"
				+ "join accounts on transfers.account_from = accounts.account_id\n"
				+ "join users on accounts.user_id = users.user_id\n"
				+ "join accounts as acc on transfers.account_to = acc.account_id\n"
				+ "join users as userscopy on acc.user_id = userscopy.user_id\n"
				+ "join transfer_statuses on transfers.transfer_status_id = transfer_statuses.transfer_status_id\n"
				+ "WHERE account_from = ? OR account_to = ?\n"
				+ "ORDER BY transfer_id";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sqlTransactionDetails, id, id);
		while(result.next()) {
			TransactionHistory transaction = mapRowToTransaction(result);
			transactions.add(transaction);
			
		}
		return transactions;
	}

	private TransactionHistory mapRowToTransaction(SqlRowSet result) {
		TransactionHistory transaction = new TransactionHistory();
		transaction.setTransferId(result.getInt("transfer_id"));
		transaction.setTransfer_type_id(result.getInt("transfer_type_id"));
		transaction.setTransfer_status_desc(result.getString("transfer_status_desc"));
		transaction.setSenderName(result.getString("sender"));
		transaction.setReceiverName(result.getString("receiver"));
		transaction.setAmount(result.getBigDecimal("amount"));

		return transaction;

	}

}
