package cs2103;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * This class is used to manipulate a text file. It supports 6 basic commands:
 * add, delete, display, clear, save and exit
 * 
 * @author Victor Hazali
 */
public class TextBuddy {

	private static final String	WELCOME_MESSAGE			= "Welcome to TextBuddy. %1$s is ready for use";
	private static final String	MESSAGE_FILE_DELETED	= "Deleted from %1$s: \"%2$s\"";
	private static final String	MESSAGE_ALL_DELETED		= "All content deleted from %1$s";
	private static final String	MESSAGE_FILE_ADDED		= "Added to %1$s: \"%2$s\"";
	private static final String	MESSAGE_FILE_EMPTY		= "%1$s is empty";
	private static final String	MESSAGE_INVALID_COMMAND	= "%1$s is an invalid command format";
	private static final String	MESSAGE_FILE_IO_ERROR	= "Failed to open %1$s";
	private static final String	MESSAGE_FILE_SAVED		= "%1$s successfully saved";

	// Acceptable commands from user:
	enum USER_COMMAND {
		ADD, DISPLAY, DELETE, INVALID, CLEAR, SAVE, EXIT
	};

	private static Scanner				scanner	= new Scanner(System.in);
	private static LinkedList<String>	m_lines	= new LinkedList<String>();
	private static String				m_fileName;

	public static void main(String[] args) {
		m_fileName = args[0];
		readFile(m_fileName);
		System.out.println(String.format(WELCOME_MESSAGE, m_fileName));
		while (true) {
			System.out.print("command: ");
			String userCommand = scanner.nextLine();
			executeCommand(userCommand);
		}
	}

	/**
	 * Reads the contents of file and store them in a linked list
	 * 
	 * @param fileName
	 *            name of file to read from
	 */
	private static void readFile(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String nextLine = reader.readLine();
			while (nextLine != null) {
				m_lines.add(nextLine);
				nextLine = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			newFile(fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new file with nothing inside
	 * 
	 * @param fileName
	 *            name of file to be created
	 */
	private static void newFile(String fileName) {
		try {
			FileWriter writer = new FileWriter(fileName);
			writer.write("");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * executes the user's command. Command has to be either one of: add,
	 * delete, display, clear, save or exit
	 * 
	 * @param userCommand
	 *            user's input
	 */
	private static void executeCommand(String userCommand) {
		if (userCommand.trim().equals("")) {
			System.out.println(String.format(MESSAGE_INVALID_COMMAND,
					userCommand));
			return;
		}

		USER_COMMAND commandType = determineCommandType(userCommand);

		switch (commandType) {
			case ADD :
				addText(userCommand);
				break;
			case DISPLAY :
				displayContent(userCommand);
				break;
			case DELETE :
				deleteText(userCommand);
				break;
			case CLEAR :
				clearFile(userCommand);
				break;
			case SAVE :
				saveFile();
				break;
			case INVALID :
				System.out.println(String.format(MESSAGE_INVALID_COMMAND,
						userCommand));
				break;
			case EXIT :
				saveFile();
				System.exit(0);
			default:
				throw new Error("Unrecognized command type");
		}
	}

	private static USER_COMMAND determineCommandType(String userCommand) {
		if (userCommand == null) {
			throw new Error("User command cannot be null!");
		}

		String commandType = getFirstWord(userCommand);
		if (commandType.equalsIgnoreCase("add")) {
			return USER_COMMAND.ADD;
		} else if (commandType.equalsIgnoreCase("delete")) {
			return USER_COMMAND.DELETE;
		} else if (commandType.equalsIgnoreCase("display")) {
			return USER_COMMAND.DISPLAY;
		} else if (commandType.equalsIgnoreCase("exit")) {
			return USER_COMMAND.EXIT;
		} else if (commandType.equalsIgnoreCase("clear")) {
			return USER_COMMAND.CLEAR;
		} else if (commandType.equalsIgnoreCase("save")) {
			return USER_COMMAND.SAVE;
		} else {
			return USER_COMMAND.INVALID;
		}
	}

	/**
	 * adds a string of text into the file
	 * 
	 * @param userCommand
	 *            should contain the command 'add' and the new line of text the
	 *            user wishes to add to the file
	 */
	private static void addText(String userCommand) {
		String text = removeFirstWord(userCommand);
		m_lines.add(text);
		System.out.println(String.format(MESSAGE_FILE_ADDED, m_fileName, text));
	}

	/**
	 * displays content of text file
	 * 
	 * @param userCommand
	 */
	private static void displayContent(String userCommand) {
		if (m_lines.size() < 1) {
			System.out.println(String.format(MESSAGE_FILE_EMPTY, m_fileName));
		} else {
			for (int i = 0; i < m_lines.size(); i++) {
				System.out.println((i + 1) + ". " + m_lines.get(i));
			}
		}
	}

	/**
	 * delete text according to user's selection
	 * 
	 * @param userCommand
	 *            should contain the command delete and line number
	 */
	private static void deleteText(String userCommand) {
		String text = removeFirstWord(userCommand);
		try {
			Integer choice = Integer.parseInt(text) - 1;
			String deleted = m_lines.get(choice);
			m_lines.remove(choice.intValue());
			System.out.println(String.format(MESSAGE_FILE_DELETED, m_fileName,
					deleted));
		} catch (NumberFormatException nfe) {
			System.out.println(String.format(MESSAGE_INVALID_COMMAND,
					userCommand));
		}
	}

	/**
	 * clears content of text file
	 * 
	 * @param userCommand
	 */
	private static void clearFile(String userCommand) {
		m_lines = new LinkedList<String>();
		System.out.println(String.format(MESSAGE_ALL_DELETED, m_fileName));
	}

	/**
	 * writes the current lines into the file
	 */
	private static void saveFile() {
		try {
			FileWriter writer = new FileWriter(m_fileName);
			for (String lines : m_lines) {
				writer.write(lines + "\n");
			}
			writer.close();
			System.out.println(String.format(MESSAGE_FILE_SAVED, m_fileName));
		} catch (IOException e) {
			System.out
					.println(String.format(MESSAGE_FILE_IO_ERROR, m_fileName));
		}
	}

	/**
	 * gets the first word from a String
	 * 
	 * @param userCommand
	 *            string of text
	 * @return the first word from userCommand
	 */
	private static String getFirstWord(String userCommand) {
		String firstWord = userCommand.trim().split("\\s+")[0];
		return firstWord;
	}

	/**
	 * Removes the first word from a String
	 * 
	 * @param userCommand
	 *            string to manipulate
	 * @return userCommand without the first word
	 */
	private static String removeFirstWord(String userCommand) {
		String parameters = userCommand.replace(getFirstWord(userCommand), "")
				.trim();
		return parameters;
	}
}