package eu.vdrm.utils.cmdline;

import java.util.Scanner;

public class App 
{
	private static final Scanner scanner = new Scanner(System.in);
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        App app = new App();
        app.mainMenu();
        System.out.println( "Good Bye!" );
    }

	private void mainMenu() {
		int[] okc = new int[] {1,9};
		Common.out("1 FileUtils");
		Common.out("9 Exit");
		Common.out("Make your choice");
		while (true) {
			int choice = scanner.nextInt();
			if (choiceOK(choice,okc)) {
				switch (choice) {
				case 9: 
					return;
				case 1:
					doFileMenu();
					break;
				}
			}
		}
	}
	
	private void doFileMenu() {
		int[] okc = new int[] {1,9};
		FileActions actions = new FileActions();
		Common.out("1 NewestFile");
		Common.out("9 Exit");
		Common.out("Make your choice");
		while (true) {
			int choice = scanner.nextInt();
			if (choiceOK(choice,okc)) {
				switch (choice) {
				case 9: 
					return;
				case 1:
					actions.doNewest();
				}
			}
		}
	}

	private boolean choiceOK(int choice, int[] okc) {
		for (int i = 0; i < okc.length; i++) {
			if (okc[i] == choice) {
				return true;
			}
		}
		Common.out("Unknown choice: " + choice);
		return false;
	}

	
}
