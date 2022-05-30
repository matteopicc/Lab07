package it.polito.tdp.poweroutages.DAO;

import java.sql.Connection;
import it.polito.tdp.poweroutages.model.NercIdMap;

public class TestPowerOutagesDAO {

	public static void main(String[] args) {
		
		try {
			Connection connection = ConnectDB.getConnection();
			connection.close();
			System.out.println("Connection Test PASSED");
			
			PowerOutageDAO dao = new PowerOutageDAO() ;
			NercIdMap a = new NercIdMap();
			System.out.println(dao.getNercList(a)) ;
			System.out.println(dao.getPowerOutageList(a)) ;

		} catch (Exception e) {
			System.err.println("Test FAILED");
		}
		
		
	}

}
