package com.rau.evoting.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.rau.evoting.models.UserGroup;
import com.restfb.types.Group;

public class UserGroupDP {

	public static final String TABLE_NAME = "UserGroups";
	
	
	public static final String USER_ID = "userId";
	public static final String GROUP_ID = "groupId";
	public static final String NAME = "name";
	
	public static  void insert(int userId, List<UserGroup> groups) {
		Connection con = null;
		try {
			con = SqlDataProvider.getInstance().getConnection();
			con.setAutoCommit(false);
			String sql = "delete from " + TABLE_NAME + " where " + USER_ID + " = ?";
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setInt(1, userId);
			statement.executeUpdate();
			for (UserGroup gr : groups) {
				sql = "insert into " + TABLE_NAME + "(" + USER_ID + "," + GROUP_ID + "," + NAME+ ") values(?,?,?) ";
				statement = con.prepareStatement(sql);
				statement.setInt(1, userId);
				statement.setString(2, gr.getGroupId());
				statement.setString(3, gr.getName());
				statement.executeUpdate();
			}
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	
	
}
