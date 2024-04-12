package service;

import util.JDBCUtil;

/**
 * @author 송예진
 *
 */
public class BookService {
	private static BookService instance;

	private BookService() {

	}

	public static BookService getInstance() {
		if (instance == null) {
			instance = new BookService();
		}
		return instance;
	}
	JDBCUtil jdbc = JDBCUtil.getInstance();
	
	//
}
