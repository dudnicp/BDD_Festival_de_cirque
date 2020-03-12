import java.sql.SQLException;
import java.util.Scanner;

public class Exec {

	public static DataBase dataBase;
	public static Scanner scanner;

	public static void main(String[] args) {

		scanner = new Scanner(System.in);

		System.out.println("Bienvenue dans l'application d'organisation de Festivals !");

		System.out.println("USER : ");
		String user = scanner.nextLine();
		System.out.println("PASSWORD : ");
		String passwd = scanner.nextLine();

		try {
			dataBase = new DataBase(user, passwd);
		} catch (Exception e) {
			printError(e);
			System.exit(0);
		}

		boolean endExe = false;

		while (!endExe) {
			clearConsole();
			System.out.println("*** MENU PRINCIPAL ***");
			System.out.println("1 : Ajouter des artistes participants");
			System.out.println("2 : Proposer des numéros au festival");
			System.out.println("3 : Evaluer des numéros");
			System.out.println("4 : Ajouter un spectacle au festival");
			System.out.println("5 : Obtenir des informations sur le festival");
			System.out.println("6 : Quitter l'application");
			System.out.println("Votre choix :");

			int mainMenuChoice = Integer.parseInt(scanner.nextLine());

			switch (mainMenuChoice) {
				case 1:
					clearConsole();
					artistMenu();
					break;
				case 2:
					clearConsole();
					actMenu();
					break;
				case 3:
					clearConsole();
					evalMenu();
					break;
				case 4:
					clearConsole();
					showMenu();
					break;
				case 5:
					clearConsole();
					infoMenu();
					break;
				case 6:
					endExe = true;
					break;
				default:
					System.out.println("Mauvais numéro, réessayez");
			}
		}

		try {
			dataBase.deconnect();
		} catch (Exception e) {
			printError(e);
		}

		System.out.println("Au revoir !");
	}

	private static void artistMenu() {
		System.out.println("** Ajouter un artiste **");
		boolean returnToMainMenu = false;
		while (!returnToMainMenu) {
			try {
				addArtist();
				dataBase.commit();
				System.out.println("Artiste inscrit avec succès !");
			} catch (Exception e) {
				printError(e);
				dataBase.rollback();
			}
			clearConsole();
			System.out.println("Voulez vous inscrire un autre artiste au festival?");
			System.out.println("1 : Oui");
			System.out.println("2 : Non (retour au menu principal)");
			int choice = Integer.parseInt(scanner.nextLine());
			returnToMainMenu = (choice != 1);
		}
	}

	private static void addArtist() throws SQLException {
		Artist artist = new Artist();
		artist.addToDataBase();

		System.out.println("Cet artiste nécessite au moins une spécialité");
		int addSpe = 1;
		while (addSpe == 1) {
			System.out.println("Ajoutez une spécialité");
			System.out.println("Informations demandées : ");

			artist.addSpeciality();

			System.out.println("Voulez-vous ajouter une autre spécialité à cet artiste");
			System.out.println("1 : Oui");
			System.out.println("2 : Non");
			addSpe = Integer.parseInt(scanner.nextLine());
		}

		int addNickname = 1;
		while (addNickname == 1) {
			System.out.println("Voulez-vous ajouter un surnom à cet artiste ?");
			System.out.println("1 : Oui");
			System.out.println("2 : Non");
			addNickname = Integer.parseInt(scanner.nextLine());

			if (addNickname == 1) {
				System.out.println("Ajoutez un surnom");
				System.out.println("Informations demandées : ");
				artist.addNickname();
			}
		}
	}

	private static void actMenu() {
		System.out.println("** Ajouter un numero **");
		boolean returnToMainMenu = false;
		while (!returnToMainMenu) {
			try {
				addAct();
				dataBase.commit();
				System.out.println("Numéro ajouté avec succès !");
			} catch (Exception e) {
				printError(e);
				dataBase.rollback();
			}
			clearConsole();
			System.out.println("Voulez vous inscrire un autre numéro au festival?");
			System.out.println("1 : Oui");
			System.out.println("2 : Non (retour au menu principal)");
			int choice = Integer.parseInt(scanner.nextLine());
			returnToMainMenu = (choice != 1);
		}
	}

	private static void addAct() throws SQLException {
		Act newAct = new Act();
		newAct.addToDataBase();

		int choice = 1;
		while (choice == 1) {
			System.out.println("Voulez-vous ajouter un artiste participant à cet numéro ?");
			System.out.println("1 : Oui");
			System.out.println("2 : Non");
			choice = Integer.parseInt(scanner.nextLine());

			if (choice == 1) {
				System.out.println("Informations demandées : ");
				newAct.addArtist();
			}
		}
	}

	private static void evalMenu() {
		boolean returnToMainMenu = false;
		while (!returnToMainMenu) {
			System.out.println("** Evaluations **");
			System.out.println("1 : Créer un jury pour évaler un numéro");
			System.out.println("2 : Modifier une évaluation");
			System.out.println("3 : Retour au menu principal");
			System.out.println("Votre choix :");

			int choice = Integer.parseInt(scanner.nextLine());

			switch (choice) {
			case 1:
				addJury();
				returnToMainMenu = true;
				break;
			case 2:
				updateEval();
				returnToMainMenu = true;
				break;
			case 3:
				returnToMainMenu = true;
				break;
			default:
				System.out.println("Mauvais numéro, réessayez");
				break;
			}
		}
	}

	private static void addJury() {
		boolean returnToMainMenu = false;
		while (!returnToMainMenu) {
			try {
				Evaluation.createJury();
				dataBase.commit();
				System.out.println("Jury créé avec succès !");
			} catch (SQLException e) {
				printError(e);
				dataBase.rollback();
			}
			clearConsole();
			System.out.println("Voulez vous créer un jury pour un autre numéro ?");
			System.out.println("1 : Oui");
			System.out.println("2 : Non (retour au menu principal)");
			returnToMainMenu = (Integer.parseInt(scanner.nextLine()) != 1);
		}
	}

	private static void updateEval() {
		boolean returnToMainMenu = false;
		while (!returnToMainMenu) {
			try {
				Evaluation.updateEvaluation();
				dataBase.commit();
				System.out.println("Evaluation modifiée avec succès !");
			} catch (SQLException e) {
				printError(e);
				dataBase.rollback();
			}
			clearConsole();
			System.out.println("Voulez vous évaluer un autre numéro?");
			System.out.println("1 : Oui");
			System.out.println("2 : Non (retour au menu principal)");
			returnToMainMenu = (Integer.parseInt(scanner.nextLine()) != 1);
		}
	}

	private static void showMenu() {
		System.out.println("** Ajouter un Spectacle **");
		boolean returnToMainMenu = false;
		while (!returnToMainMenu) {
			try {
				addShow();
				dataBase.commit();
				System.out.println("Spectacle ajouté avec succès !");
			} catch (Exception e) {
				printError(e);
				dataBase.rollback();
			}
			clearConsole();
			System.out.println("Voulez vous ajouter un autre spectacle au festival?");
			System.out.println("1 : Oui");
			System.out.println("2 : Non (retour au menu principal)");
			int choice = Integer.parseInt(scanner.nextLine());
			returnToMainMenu = (choice != 1);
		}
	}

	private static void addShow() throws SQLException {
		Show newShow = new Show();
		newShow.addToDataBase();
		newShow.linkActsToShows();
		newShow.linkHostToShow();
	}

	private static void infoMenu() {
		boolean returnToMainMenu = false;
		while (!returnToMainMenu) {
			System.out.println("** Informations Festival **");
			System.out.println("1 : Afficher des tables ");
			System.out.println("2 : Informations sur un artiste");
			System.out.println("3 : Informations sur un numéros ");
			System.out.println("4 : Informations sur un spectacle");
			System.out.println("5 : Retour au menu principal");
			System.out.println("Votre choix :");

			int choice = Integer.parseInt(scanner.nextLine());

			switch (choice) {
				case 1:
					showTables();
					waitInput();
					break;
				case 2:
					Artist.showInfo();
					waitInput();
					break;
				case 3:
					Act.showInfo();
					waitInput();
					break;
				case 4:
					Show.showInfo();
					waitInput();
					break;
				case 5:
					returnToMainMenu = true;
					break;
				default:
					System.out.println("Mauvais numéro, réessayez");
					break;
			}
		}
	}

	private static void showTables() {
		boolean returnToInfoMenu = false;
		while (!returnToInfoMenu) {
			System.out.println("Tables à afficher :");
			System.out.println("1 : Artistes");
			System.out.println("2 : Numéros");
			System.out.println("3 : Spectacles");
			System.out.println("4 : Retour au menu informations");

			int choice = Integer.parseInt(scanner.nextLine());
			switch (choice) {
				case 1:
					clearConsole();
					try {
						dataBase.showTable("ARTISTES");
					} catch (Exception e) {
						printError(e);
					}
					break;
				case 2:
					clearConsole();
					try {
						dataBase.showTable("NUMEROS");
					} catch (Exception e) {
						printError(e);
					}					break;
				case 3:
					clearConsole();
					try {
						dataBase.showTable("SPECTACLES");
					} catch (Exception e) {
						printError(e);
					}					break;
				case 4:
					returnToInfoMenu = true;
					break;
				default:
					System.out.println("Mauvais numéro, réessayez");
					break;
			}
		}
	}


	public static void waitInput() {
		System.out.println("Appuyez sur Entrée pour continuer");
		scanner.nextLine();
	}

	public static void printError(Exception e) {
		System.out.println("Oups ! Quelque chose s'est mal passé...");
		e.printStackTrace();
		waitInput();
	}

	public static void clearConsole()
	{
		System.out.println("\f");
	}
}
