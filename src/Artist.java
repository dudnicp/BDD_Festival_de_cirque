import java.sql.*;

public class Artist {
	private int id ;
	private String lastName;
	private String firstName;
	private String birthday;
	private String circus;
	private String phoneNumber;
	
	Artist() throws SQLException {
		
		String query = "SELECT MAX(IDARTISTE) AS idMax FROM ARTISTES";
		ResultSet res = Exec.dataBase.executeQuery(query);
		if (res.next()) {			
			id = res.getInt("idMax") + 1;
		}
		else {
			id = 1;
		}
		
		Exec.dataBase.closeRes(res);
		
		System.out.println("L'ID du nouvel artiste est " + id);
		Exec.waitInput();
		
		
		System.out.println("Informations demandées :");
		
		/* NOM */
		boolean isLastNameOK = false;
		while (!isLastNameOK) {
			System.out.println("Nom : ");
			lastName = Exec.scanner.nextLine();

			char lastNameTab[] = lastName.toCharArray();
			int currentIndex = 0;
			isLastNameOK = true;
			while (currentIndex < lastNameTab.length && isLastNameOK) {
				if (Character.isDigit(lastNameTab[currentIndex])) {
					isLastNameOK = false;
				}
				currentIndex ++;
			}
			
			if (!isLastNameOK) {
				System.out.println("Nom invalide : contient des chiffres, réessayez");
			}
		}

		/* PRENOM */
		boolean isFirstNameOK = false;
		while (!isFirstNameOK) {
			System.out.println("Prénom : "); 
			firstName = Exec.scanner.nextLine();
			char firstNameTab[] = firstName.toCharArray();
			int currentIndex = 0;
			isFirstNameOK = true;
			while (currentIndex < firstNameTab.length && isFirstNameOK) {
				if (Character.isDigit(firstNameTab[currentIndex])) {
					isFirstNameOK = false;
				}
				currentIndex ++;
			}
			
			if (!isFirstNameOK) {
				System.out.println("Prénom invalide : contient des chiffres, réessayez");
			}
		}
		
		/* DATE */
	    boolean isDateOK = false;
	    while (!isDateOK) {
	    	System.out.println("Naissance (DD-MM-YYYY) : ");
	    	birthday = Exec.scanner.nextLine();
	    	if (birthday.matches("[0-9][0-9]-[0-9][0-9]-[0-9]{4}")) { 
	    	String jour = birthday.substring(0,2);
	    	String mois = birthday.substring(3,5);
	    	if (Integer.parseInt(jour) > 31 || Integer.parseInt(mois) > 12) {
	    		System.out.println("Mauvais format date : jour < 31, mois < 12, réessayez");	
	    	} else {
	    		isDateOK = true;
	    	}
	    	
	    	} else {
	    		isDateOK = false;
	    		System.out.println("Mauvais format de date : DD-MM-YYYY, réessayez");
	    	}
	    }

	    
	    /* CIRQUE */
	    System.out.println("Cirque : ");
	    circus = Exec.scanner.nextLine();
	    
	    /* TELEPHONE */
	    boolean isTelOK = false;
	    while (!isTelOK) {
	    	System.out.println("Telephone : ");
	    	phoneNumber = Exec.scanner.nextLine();
	    	if (!phoneNumber.matches("0[0-9]{9}")) {
	    		System.out.println("le numéro de téléphone doit contenir 10 chiffres et commencer par 0, réessayez");
	    		
	    	} else {
	    		isTelOK = true;
	    	}
	    }
	}
	
	public void addToDataBase() throws SQLException {	
		String query = String.format("INSERT INTO ARTISTES VALUES (%s, '%s', '%s', TO_DATE('%s', 'DD-MM-YY'), '%s', '%s')",
							id, lastName, firstName, birthday, circus, phoneNumber);
		System.out.println("Ajout de l'artiste à la base de données...");
		Exec.dataBase.executeUpdate(query);
	}
	
	public void addSpeciality() throws SQLException{
		String spe = "";
		boolean isSpeOk = false;
		while (!isSpeOk) {			
			System.out.println("Spécialité :");
			spe = (Exec.scanner.nextLine()).toUpperCase();
			String query = "SELECT NOMSPE FROM SPECIALITES WHERE NOMSPE = '" + spe + "'";
			ResultSet res = Exec.dataBase.executeQuery(query);
			if (res.next()) {
				isSpeOk = true;
			}
			else {
				System.out.println("Cette Spécialité n'est pas un des thèmes du festival, réessayez.");
			}
		}
		String query = String.format("INSERT INTO ESTSPE VALUES(%s, '%s')", id, spe);
		
		System.out.println("Association de la spécialité à l'artiste...");
		Exec.dataBase.executeUpdate(query);
	}
	
	public void addNickname() throws SQLException {
		System.out.println("Surnom :");
		String nickname = Exec.scanner.nextLine();
		String query = String.format("INSERT INTO PSEUDODE VALUES(%s, '%s')", id, nickname);
		
		System.out.println("Ajout du surnom à l'artiste...");
		Exec.dataBase.executeUpdate(query);
	}
	
	public static void showInfo(){
		try {
			System.out.println("ID Artiste :");
			int idArtist = Integer.parseInt(Exec.scanner.nextLine());
			
			Exec.clearConsole();
			
			String query = "SELECT * FROM ARTISTES WHERE IDARTISTE = " + idArtist;
			ResultSet res = Exec.dataBase.executeQuery(query);
			System.out.println("Informations sur l'artiste " + idArtist);
			Exec.dataBase.showResults(res, false);
			System.out.println();
			
			query = "SELECT NOMSPE FROM ESTSPE WHERE IDARTISTE = " + idArtist;
			res = Exec.dataBase.executeQuery(query);
			System.out.println("Liste des spécialités de l'artiste " + idArtist);
			Exec.dataBase.showResults(res, true);
			System.out.println();
			
			query = "SELECT PSEUDO FROM PSEUDODE WHERE IDARTISTE = " + idArtist;
			res = Exec.dataBase.executeQuery(query);
			System.out.println("Liste des pseudos de l'artiste " + idArtist);
			Exec.dataBase.showResults(res, true);
			System.out.println();
			
			Exec.dataBase.closeRes(res);
			
		} catch (Exception e) {
			Exec.printError(e);
		}
	}
}
