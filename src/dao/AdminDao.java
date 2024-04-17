package dao;

import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class AdminDao {
	private static AdminDao instance;

	private AdminDao() {

	}

	public static AdminDao getInstance() {
		if (instance == null) {
			instance = new AdminDao();
		}
		return instance;
	}

	JDBCUtil jdbc = JDBCUtil.getInstance();

	// 전체 책 검색 폐기된 것도 조회가능
	// 대출된 책 조회/예약된 책 조회/연체된 책조회
	/**
	 * 세션에 sel input값 pageNo pageEnd저장하고 나갈때 삭제할것
	 * 
	 * @param sel 0 : 전체 조회 > ROWNUM
	 * @param sel a : 책 폐기/사용가능한 것들만 조회 > ROWNUM '사용가능' / '폐기'
	 * @param sel b : 도서관 선택 > ROWNUM libraryList
	 * @param sel c : 분류별 선택 > ROWNUM cate
	 * @param sel d : 이름 검색 > ROWNUM name
	 * @param sel e : 출판사 검색 > ROWNUM pub
	 * @param sel f : 발행년도 검색 > ROWNUM year
	 * @param sel 1 : 대출 가능
	 * @param sel 2 : 대출 예약
	 * @param sel 3 : 대출 불가 (예약한 회원정보도 함께 출력할 것 / 연락처 필수)
	 * 
	 */
	public List<Map<String, Object>> bookAdminList(List<Object> param, String sel) {
		String sql = "SELECT * \r\n" + "FROM (\r\n" + "\r\n" + "SELECT \r\n" + "    ROWNUM AS RN, \r\n"
				+ "    A.BOOK_NO, \r\n" + "    A.BOOK_NAME, \r\n" + "    A.BOOK_AUTHOR, A.BOOK_REMARK, \r\n" + "    A.BOOK_PUB, \r\n"
				+ "    A.BOOK_PUB_YEAR, \r\n" + "    B.CATE_NAME, \r\n" + "    C.LIB_NAME, \r\n"
				+ "    D.RENT_DATE, \r\n" + "    D.RETURN_DATE, \r\n" + "    F.MEM_NAME, \r\n" + "    F.MEM_TELNO\r\n"
				+ "FROM \r\n" + "    BOOK A\r\n" + "    INNER JOIN BOOK_CATEGORY B ON A.CATE_NO = B.CATE_NO\r\n"
				+ "    INNER JOIN LIBRARY C ON A.LIB_NO = C.LIB_NO\r\n"
				+ "    LEFT JOIN BOOK_RENT D ON A.BOOK_NO = D.BOOK_NO\r\n"
				+ "    LEFT JOIN BOOK_REF E ON D.BOOK_REF_NO = E.BOOK_REF_NO\r\n"
				+ "    LEFT JOIN MEMBER F ON D.MEM_NO = F.MEM_NO\r\n" + "WHERE A.BOOK_NO>00000\r\n";
		if (!sel.contains("0")) {
			if (sel.contains("a")) {
				sql += "          AND A.BOOK_REMARK =?\r\n";
			}
			if (sel.contains("b")) {
				sql += "         AND A.LIB_NO = ?\r\n";
			}
			if (sel.contains("c")) {
				sql += "       AND A.CATE_NO = ?\r\n";
			}
			if (sel.contains("d")) {
				sql += "        AND A.BOOK_NAME LIKE '%' || ? || '%'\r\n";
			}
			if (sel.contains("e")) {
				sql += "       AND A.BOOK_PUB LIKE '%' || ? || '%'\r\n";
			}
			if (sel.contains("f")) {
				sql += "       AND A.BOOK_PUB_YEAR LIKE '%' || ? || '%'\r\n";
			}
			if (sel.contains("g")) {
				sql += "       AND A.BOOK_NO = ?\r\n";
			}
			if (sel.contains("3")) {
				sql += "       AND D.RENT_DATE IS NOT NULL\r\n" + "   AND D.RETURN_YN = 'N'\r\n";
			}
			if (sel.contains("2")) {
				sql += "       AND D.BOOK_REF_CHK = 1\r\n";
			}
			if (sel.contains("1")) {
				sql += "        AND A.BOOK_NO NOT IN (\r\n" + "            SELECT A.BOOK_NO\r\n"
						+ "            FROM \r\n" + "                BOOK A\r\n"
						+ "                INNER JOIN BOOK_CATEGORY B ON A.CATE_NO = B.CATE_NO\r\n"
						+ "                INNER JOIN LIBRARY C ON A.LIB_NO = C.LIB_NO\r\n"
						+ "                LEFT JOIN BOOK_RENT D ON A.BOOK_NO = D.BOOK_NO\r\n"
						+ "                LEFT JOIN BOOK_REF E ON D.BOOK_REF_NO = E.BOOK_REF_NO\r\n"
						+ "                LEFT JOIN MEMBER F ON D.MEM_NO = F.MEM_NO\r\n" + "            WHERE \r\n"
						+ "                D.RENT_DATE IS NOT NULL\r\n" + "                AND D.RETURN_YN = 'N'\r\n"
						+ "        )\r\n" + "        AND A.BOOK_NO NOT IN (\r\n" + "            SELECT A.BOOK_NO\r\n"
						+ "            FROM \r\n" + "                BOOK A\r\n"
						+ "                INNER JOIN BOOK_CATEGORY B ON A.CATE_NO = B.CATE_NO\r\n"
						+ "                INNER JOIN LIBRARY C ON A.LIB_NO = C.LIB_NO\r\n"
						+ "                LEFT JOIN BOOK_RENT D ON A.BOOK_NO = D.BOOK_NO\r\n"
						+ "                LEFT JOIN BOOK_REF E ON D.BOOK_REF_NO = E.BOOK_REF_NO\r\n"
						+ "                LEFT JOIN MEMBER F ON D.MEM_NO = F.MEM_NO\r\n" + "            WHERE \r\n"
						+ "                D.BOOK_REF_CHK = 1\r\n" + "        )\r\n";
			}
		}
		sql += ") \r\n" + "WHERE \r\n" + "    RN >= ? AND RN <= ?";
		return jdbc.selectList(sql, param);
	}
	
	public Map<String, Object> bookAdminListCount (List<Object> param, String sel) {
		String sql = "SELECT count(*) AS COUNT \r\n" + "FROM (\r\n" + "\r\n" + "SELECT \r\n" + "    ROWNUM AS RN, \r\n"
				+ "    A.BOOK_NO, \r\n" + "    A.BOOK_NAME, \r\n" + "    A.BOOK_AUTHOR, \r\n" + "    A.BOOK_PUB, \r\n"
				+ "    A.BOOK_PUB_YEAR, \r\n" + "    B.CATE_NAME, \r\n" + "    C.LIB_NAME, \r\n"
				+ "    D.RENT_DATE, \r\n" + "    D.RETURN_DATE, \r\n" + "    F.MEM_NAME, \r\n" + "    F.MEM_TELNO\r\n"
				+ "FROM \r\n" + "    BOOK A\r\n" + "    INNER JOIN BOOK_CATEGORY B ON A.CATE_NO = B.CATE_NO\r\n"
				+ "    INNER JOIN LIBRARY C ON A.LIB_NO = C.LIB_NO\r\n"
				+ "    LEFT JOIN BOOK_RENT D ON A.BOOK_NO = D.BOOK_NO\r\n"
				+ "    LEFT JOIN BOOK_REF E ON D.BOOK_REF_NO = E.BOOK_REF_NO\r\n"
				+ "    LEFT JOIN MEMBER F ON D.MEM_NO = F.MEM_NO\r\n" + "WHERE A.BOOK_NO>00000\r\n";
		if (!sel.contains("0")) {
			if (sel.contains("a")) {
				sql += "          AND A.BOOK_REMARK =?\r\n";
			}
			if (sel.contains("b")) {
				sql += "         AND A.LIB_NO = ?\r\n";
			}
			if (sel.contains("c")) {
				sql += "       AND A.CATE_NO = ?\r\n";
			}
			if (sel.contains("d")) {
				sql += "        AND A.BOOK_NAME LIKE '%' || ? || '%'\r\n";
			}
			if (sel.contains("e")) {
				sql += "       AND A.BOOK_PUB LIKE '%' || ? || '%'\r\n";
			}
			if (sel.contains("f")) {
				sql += "       AND A.BOOK_PUB_YEAR LIKE '%' || ? || '%'\r\n";
			}
			if (sel.contains("g")) {
				sql += "       AND A.BOOK_NO = ?\r\n";
			}
			if (sel.contains("3")) {
				sql += "       AND D.RENT_DATE IS NOT NULL\r\n" + "   AND D.RETURN_YN = 'N'\r\n";
			}
			if (sel.contains("2")) {
				sql += "       AND D.BOOK_REF_CHK = 1\r\n";
			}
			if (sel.contains("1")) {
				sql += "        AND A.BOOK_NO NOT IN (\r\n" + "            SELECT A.BOOK_NO\r\n"
						+ "            FROM \r\n" + "                BOOK A\r\n"
						+ "                INNER JOIN BOOK_CATEGORY B ON A.CATE_NO = B.CATE_NO\r\n"
						+ "                INNER JOIN LIBRARY C ON A.LIB_NO = C.LIB_NO\r\n"
						+ "                LEFT JOIN BOOK_RENT D ON A.BOOK_NO = D.BOOK_NO\r\n"
						+ "                LEFT JOIN BOOK_REF E ON D.BOOK_REF_NO = E.BOOK_REF_NO\r\n"
						+ "                LEFT JOIN MEMBER F ON D.MEM_NO = F.MEM_NO\r\n" + "            WHERE \r\n"
						+ "                D.RENT_DATE IS NOT NULL\r\n" + "                AND D.RETURN_YN = 'N'\r\n"
						+ "        )\r\n" + "        AND A.BOOK_NO NOT IN (\r\n" + "            SELECT A.BOOK_NO\r\n"
						+ "            FROM \r\n" + "                BOOK A\r\n"
						+ "                INNER JOIN BOOK_CATEGORY B ON A.CATE_NO = B.CATE_NO\r\n"
						+ "                INNER JOIN LIBRARY C ON A.LIB_NO = C.LIB_NO\r\n"
						+ "                LEFT JOIN BOOK_RENT D ON A.BOOK_NO = D.BOOK_NO\r\n"
						+ "                LEFT JOIN BOOK_REF E ON D.BOOK_REF_NO = E.BOOK_REF_NO\r\n"
						+ "                LEFT JOIN MEMBER F ON D.MEM_NO = F.MEM_NO\r\n" + "            WHERE \r\n"
						+ "                D.BOOK_REF_CHK = 1\r\n" + "        )\r\n";
			}
		}
		sql += ") \r\n";
		return jdbc.selectOne(sql, param);
	}
	


	// 책 추가

	/*
	 * 카테고리 리스트를 뽑아서 누르기 List<Map<String, Object>> cate = bookService.cateName();
	 * printCateName(cate);
	 * 
	 * librarylist() > LibraryController 에서 뽑아서 직접 선택하게 할 것
	 * 
	 */
	/**
	 * 모든 정보를 추가한 후 확인절차를 마친 뒤 저장하기 (while 문 break) 책 내용은 이후 수정할 수 없습니다 정확히 작성하고
	 * 저장해주세요
	 * 
	 * @param BOOK_NAME, BOOK_AUTHOR, BOOK_PUB, BOOK_PUB_YEAR, LIB_NO
	 * @param cateNo
	 */
	public void bookInsert(List<Object> param, int cateNo) {
		String sql = "INSERT INTO BOOK (BOOK_NO, BOOK_NAME, BOOK_AUTHOR, BOOK_PUB, BOOK_PUB_YEAR, CATE_NO, LIB_NO)\r\n"
				+ "SELECT MAX(BOOK_NO)+1,"
				+ "?, ?, ?, ?, " + cateNo + ", ?\r\n" + "FROM BOOK\r\n" + "WHERE CATE_NO="
				+ cateNo;
		jdbc.update(sql, param);
	}
	
	/**책 이관
	 * @param LIB_NO BOOK_NO
	 */
	public void bookEscalation(List<Object> param) {
		String sql = "UPDATE BOOK\r\n" + 
				"SET LIB_NO=?\r\n" + 
				"WHERE BOOK_NO=?";
		jdbc.update(sql, param);
	}
	

	/**책 상태 변경
	 * @param BOOK_REMARK, BOOK_NO
	 */
	public void bookStateUpdate(List<Object> param) {
		String sql = "UPDATE BOOK\r\n" + 
				"SET BOOK_REMARK=?\r\n" + 
				"WHERE BOOK_NO=?";
		jdbc.update(sql, param);
	}

	public Map<String, Object> CateName(int cateNo){
		String sql="SELECT CATE_NAME\r\n" + 
				"FROM BOOK_CATEGORY\r\n" + 
				"WHERE CATE_NO="+cateNo;
		return jdbc.selectOne(sql);
	}
	
	public Map<String,Object> libName (int libNo){
		String sql = "SELECT LIB_NAME\r\n" + 
				"FROM LIBRARY\r\n" + 
				"WHERE LIB_NO="+libNo;
		return jdbc.selectOne(sql);
	}
}
