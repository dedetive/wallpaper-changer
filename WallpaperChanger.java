import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.NativeHookException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

import java.io.File;
import java.util.*;

public class WallpaperChanger extends Thread implements NativeKeyListener {
	private static Timer timer;
	private static TimerTask wallpaperTask;

	public interface User32 extends Library {
		User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);
		boolean SystemParametersInfo(int one, int two, String s, int three);
	}

	/*
	 * INSERT BELOW YOUR PARAMETERS
	 * The name has to be exact
	 * Subfolders must be contained within wallpaperFolder
	 *
	 * Additional subfolders can be created with minor alterations
	 * Those are within main()'s "list" case, isWallpaperFile(), findWallpaperFile(), changeWallpaper()
	 */

	private static String wallpaperFolder = "C:\\Users\\ded\\Pictures\\wallpapers\\good_wallpapers";
	private static final String subfolder1 = "\\decent_wallpapers";
	private static final String subfolder2 = "\\good_wallpapers";
	private static final String subfolder3 = "\\amazing_wallpapers";
	private static final String defaultWallpaperPath = "C:\\Users\\ded\\Pictures\\wallpapers\\rabbito.jpg";
	private static final double chanceToSubfolder1 = 0.2; // These must be between 0 and 1
	private static final double chanceToSubfolder2 = 0.6; // This must be higher than chanceToSubfolder1
	private static int changeIntervalMS = 600000; // Change wallpaper every ten minutes (600000 ms)

	// END PARAMETERS

	private static final String wallpaperFolderParent = wallpaperFolder;
	private static boolean isFrozen = false;
	private static boolean defaultMode = false;
	private static boolean exitingDefaultMode = false;
	private static File lastSelectedFile;

	public static void main(String[] args) {

		WallpaperChanger thread = new WallpaperChanger();
		thread.start();

		new Thread(() -> {
			Scanner scanner = new Scanner(System.in);
			while (true) {
				String input = scanner.nextLine();

				// CHANGE HERE FOR SUBFOLDERS

				if (input.equalsIgnoreCase("list")) {
					System.out.println("\n  " + subfolder1 + " \n");
					listWallpapers(subfolder1);
					System.out.println("\n  " + subfolder2 + " \n");
					listWallpapers(subfolder2);
					System.out.println("\n  " + subfolder3 + " \n");
					listWallpapers(subfolder3);

				// END CHANGE

				} else {
					if (isWallpaperFile(input)) {
						System.out.println("Changing wallpaper to: " + input);
						changeWallpaperToSpecificFile(input);
					} else if (input.equalsIgnoreCase("close")) {
						System.out.println("Closing app and changing into default wallpaper...");
						closeApp();
					} else if (input.equalsIgnoreCase("freeze")) {
						if (!isFrozen) {
							System.out.println("Freezing current wallpaper...");
						} else {
							System.out.println("Unfreezing current wallpaper...");
						}
						freezeWallpaper();
					} else if (input.equalsIgnoreCase("reroll") || input.equalsIgnoreCase("re-roll") || input.equalsIgnoreCase("random")) {
						System.out.println("Re-rolling the wallpaper...");
						rerollWallpaper();
					} else if (input.equalsIgnoreCase("default")) {
						System.out.println("Changing into default wallpaper...");
						changeToDefault();
					} else if (input.equalsIgnoreCase("up")) {
						increaseDelay();
						System.out.println("Increasing delay by 15%. Currently at " + (float) changeIntervalMS/1000 + "s...");
					} else if (input.equalsIgnoreCase("down")) {
						decreaseDelay();
						System.out.println("Decreasing delay by 15%. Currently at " + changeIntervalMS + "...");
					} else if (input.equalsIgnoreCase("version") || input.equalsIgnoreCase("v")) {
						System.out.println("Version 1.0.0");
					} else if (input.equalsIgnoreCase("help") || input.equals("?")) {
						System.out.println("""
								
								List of available commands:
								- help -> Shows the available commands.
								- version -> Shows the current version of the program.
								- list -> Shows a list of every wallpaper in the directory.
								- wallpaper name.extension -> Changes wallpaper to the selected wallpaper.
								- reroll (Ctrl + Alt + R) -> Re-rolls into a random wallpaper.
								- freeze (Ctrl + Alt + F) -> While frozen, does not change wallpaper automatically.
								- default (Ctrl + Alt + D) -> Reverts wallpaper into the default.
								- up (Ctrl + Alt + NUMPAD 8) -> Increases wallpaper changing delay by 15%.
								- down (Ctrl + Alt + NUMPAD 2) -> Decreases wallpaper changing delay by 15%.
								- close (Ctrl + Alt + W) -> Closes this app and reverts wallpaper to the default.
								
								""");
					} else {
						System.out.println("Command not recognized or file not found. Type 'help' or ? for help.");
					}
				}
			}
		}).start();
	}

	private static void changeToDefault() {
		openDefaultWallpaper();
		if (!isFrozen) {
			resetWallpaperTimer(changeIntervalMS);
		}
	}

	private static boolean isWallpaperFile(String filename) {

		// CHANGE HERE FOR SUBFOLDERS

		return isFileInFolder(subfolder1, filename) || isFileInFolder(subfolder2, filename) || isFileInFolder(subfolder3, filename);

		// END
	}

	private static boolean isFileInFolder(String subfolder, String filename) {
		File wallpaperDirectory = new File(wallpaperFolderParent + subfolder);
		File[] files = wallpaperDirectory.listFiles((_, name) -> name.equalsIgnoreCase(filename));

		return files != null && files.length > 0;
	}

	private static void changeWallpaperToSpecificFile(String filename) {
		File wallpaperFile = findWallpaperFile(filename);
		if (wallpaperFile != null) {
			boolean result = User32.INSTANCE.SystemParametersInfo(0x0014, 0, wallpaperFile.getAbsolutePath(), 1);
			if (!result) {
				System.err.println("Failed to update the wallpaper with " + filename + " ");
			} else {
				System.out.println("Wallpaper updated to " + filename + " successfully at " +
						(String.valueOf(java.time.LocalTime.now())).substring(0, 8) + ". `");
				if (!isFrozen) {
					resetWallpaperTimer(changeIntervalMS);
				}
			}
		} else {
			System.err.println("Wallpaper file not found: " + filename);
		}
	}

	private static File findWallpaperFile(String filename) {

		// CHANGE HERE FOR SUBFOLDERS

		File[] wallpapersSubfolder1 = new File(wallpaperFolderParent + subfolder1).listFiles((_, name) -> name.equalsIgnoreCase(filename));
		File[] wallpapersSubfolder2 = new File(wallpaperFolderParent + subfolder2).listFiles((_, name) -> name.equalsIgnoreCase(filename));
		File[] wallpapersSubfolder3 = new File(wallpaperFolderParent + subfolder3).listFiles((_, name) -> name.equalsIgnoreCase(filename));

		// ALSO CHANGE HERE

		if (wallpapersSubfolder1 != null && wallpapersSubfolder1.length > 0) {
			return wallpapersSubfolder1[0];
		} else if (wallpapersSubfolder2 != null && wallpapersSubfolder2.length > 0) {
			return wallpapersSubfolder2[0];
		} else if (wallpapersSubfolder3 != null && wallpapersSubfolder3.length > 0) {
			return wallpapersSubfolder3[0];
		}

		// END

		return null;
	}

	private static void listWallpapers(String subfolder) {
		File wallpaperDirectory = new File(wallpaperFolderParent + subfolder);
		File[] files = wallpaperDirectory.listFiles((_, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));
		assert files != null;
		for (File file : files) {
			System.out.println(file.getName());
		}
	}

	public void run() {

		startWallpaperTimer();

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		GlobalScreen.addNativeKeyListener(new WallpaperChanger());
	}

	private static void startWallpaperTimer() {
		timer = new Timer();
		wallpaperTask = new TimerTask() {
			@Override
			public void run() {
				changeWallpaper();
			}
		};
		timer.scheduleAtFixedRate(wallpaperTask, 0, changeIntervalMS);
	}

	private static void resetWallpaperTimer(int offset) {
		if (timer != null) {
			timer.cancel();
			wallpaperTask.cancel();
		}

		timer = new Timer();
		wallpaperTask = new TimerTask() {
			@Override
			public void run() {
				if (!isFrozen) {
					changeWallpaper();
				}
			}
		};

		timer.scheduleAtFixedRate(wallpaperTask, offset, changeIntervalMS);
	}

	private static void changeWallpaper() {
		if (!defaultMode) {

			// CHANGE HERE FOR SUBFOLDERS

			double randomNumber = Math.random();
			if (randomNumber <= chanceToSubfolder1) {
				wallpaperFolder += subfolder1;
			} else if (randomNumber <= chanceToSubfolder2) {
				wallpaperFolder += subfolder2;
			} else {
				wallpaperFolder += subfolder3;
			}

			// END

			File wallpaperDirectory = new File(wallpaperFolder);
			File[] files = wallpaperDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

			if (files != null && files.length > 0) {
				File selectedFile;

				defaultMode = false;
				if (exitingDefaultMode) {
					selectedFile = lastSelectedFile;
					exitingDefaultMode = false;
				} else {
					Random random = new Random();
					selectedFile = files[random.nextInt(files.length)];
				}
				lastSelectedFile = selectedFile;

				boolean result = User32.INSTANCE.SystemParametersInfo(0x0014, 0, selectedFile.getAbsolutePath(), 1);
				if (!result) {
					System.err.println("Failed to update the wallpaper with " + selectedFile.getName());
				} else {
					System.out.println("Wallpaper updated to " + selectedFile.getName() + " successfully at " +
							(String.valueOf(java.time.LocalTime.now())).substring(0, 8) + ".");
				}
			} else {
				System.err.println("No valid images found in the wallpaper folder.");
			} wallpaperFolder = wallpaperFolderParent;
		}
	}

	private static void closeApp() {
		defaultMode = false;
		openDefaultWallpaper();
		System.exit(0);
	}

	private static void freezeWallpaper() {
		isFrozen = !isFrozen;
		if (!isFrozen) {
			resetWallpaperTimer(changeIntervalMS);
		}
	}

	private static void rerollWallpaper() {
		defaultMode = false;
		resetWallpaperTimer(0);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VC_W && // Ctrl + Alt + W = Close app
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Ctrl") &&
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Alt")) {
			System.out.println("Ctrl + Alt + W detected! Closing app and changing into default wallpaper...");
			closeApp();
		} else if (e.getKeyCode() == NativeKeyEvent.VC_F && // Ctrl + Alt + F = Freeze/unfreeze wallpaper
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Ctrl") &&
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Alt")) {
			if (!isFrozen) {
				System.out.println("Ctrl + Alt + F detected! Freezing current wallpaper...");
			} else {
				System.out.println("Ctrl + Alt + F detected! Unfreezing current wallpaper...");
			}
			freezeWallpaper();
		} else if (e.getKeyCode() == NativeKeyEvent.VC_R && // Ctrl + Alt + R = Re-roll wallpaper
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Ctrl") &&
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Alt")) {
			System.out.println("Ctrl + Alt + R detected! Re-rolling the wallpaper...");
			rerollWallpaper();
		} else if (e.getKeyCode() == NativeKeyEvent.VC_D && // Ctrl + Alt + D = Default wallpaper
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Ctrl") &&
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Alt")) {
			System.out.println("Ctrl + Alt + D detected! Changing into default wallpaper...");
			changeToDefault();
		} else if (e.getKeyCode() == NativeKeyEvent.VC_8 && // Ctrl + Alt + NUMPAD 8 = Increase delay
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Ctrl") &&
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Alt")) {
			increaseDelay();
			System.out.println("Ctrl + Alt + 8 detected! Increasing delay by 15%. Currently at " + (float) changeIntervalMS/1000 + "s...");
		} else if (e.getKeyCode() == NativeKeyEvent.VC_2 && // Ctrl + Alt + NUMPAD 2 = Decrease delay
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Ctrl") &&
				NativeKeyEvent.getModifiersText(e.getModifiers()).contains("Alt")) {
			decreaseDelay();
			System.out.println("Ctrl + Alt + 2 detected! Decreasing delay by 15%. Currently at " + (float) changeIntervalMS/1000 + "s...");
		}
	}

	private static void increaseDelay() {
		changeIntervalMS += changeIntervalMS * 15/100;
		resetWallpaperTimer(changeIntervalMS);
	}

	private static void decreaseDelay() {
		changeIntervalMS -= changeIntervalMS * 15/100;
		resetWallpaperTimer(changeIntervalMS);
	}

	private static void openDefaultWallpaper() {

		User32.INSTANCE.SystemParametersInfo(0x0014, 0, defaultWallpaperPath, 1);

		if (!defaultMode) {
			defaultMode = true;
			exitingDefaultMode = false;
		} else {
			defaultMode = false;
			exitingDefaultMode = true;
			changeWallpaper();
			resetWallpaperTimer(changeIntervalMS);
		}
	}
}
