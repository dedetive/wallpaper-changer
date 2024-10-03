# Wallpaper Changer
A program that changes your wallpaper, with some tweaks.

This was made by me because I'm not allowed to have whatever wallpaper I wish to have because of strict parenting, so it's centered around having a default wallpaper that I'm allowed to use and those within the good_wallpapers folder are those I'm not allowed to use.
This only supports .png and .jpg, although it's fairly easy to implement other image extensions. I, however, did not test it.

This program can change your wallpaper by picking random files from one or more folders, with a specific weight by subfolder. These are the available commands:

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

Ctrl + Alt + LETTER commands work even if the app is not focused.

To customize the program for your own use case, you must edit the first few lines of variables and, to change the number of subfolders, alter a few command lines.
```java
private static String wallpaperFolder = "C:\\Users\\ded\\Pictures\\wallpapers\\good_wallpapers";
	private static final List<String> subfolders = Arrays.asList(new String[]{
		"\\decent_wallpapers",      // Here is where you must manipulate subfolders
		"\\good_wallpapers",
		"\\amazing_wallpapers"
	});
	private static final String defaultWallpaperPath = "C:\\Users\\ded\\Pictures\\wallpapers\\rabbito.jpg";
	private static final List<Double> chanceToSubfolders = Arrays.asList(new Double[]{
		0.25,   	  // These values must be between 0 and 1
		0.6,    	  // All numbers must be in order, and it reflects directly the chance of the respective subfolder
		1.0       	  // The last number must always be 1.0, otherwise code will break given the right circumstances
	});		          // Also the number of elements in this list must always be equal to the number of subfolders
	private static int changeIntervalMS = 600000; // Changes wallpaper every ten minutes (600000 ms)
```

Paths must have double backslashes.

- wallpaperFolder is the path to the folder that contains subfolders with the images.
- subfolders are the subfolders within wallpaperFolder.
- defaultWallpaperPath is the full path to where the default picture is located. It must be a file.
- chanceToSubfolders are the chances for the random number generator to pick a file from the first subfolder in subfolders. This case means there's a 25% \(0.25 * 100%\) to be a file in subfolders\[0\], 35% \(\(0.6 - 0.25\) * 100%\) to be a file in subfolders\[1\] and lastly the ramaining 40% \(\(1.0 - 0.6\) * 100%\) to be a file in subfolders\[2\].
- changeIntervalMS is the default interval in milliseconds to change the wallpaper. Ctrl + Alt + 2 and 8 may affect this.

To change the number of subfolders, you must:
1. In the parameters section at the top of the code, change \\decent_wallpapers, \\good_wallpapers and \\amazing_wallpapers to whatever subfolder names you'd like. There must be double backslashes.
```java
private static final List<String> subfolders = Arrays.asList(new String[]{
	"\\decent_wallpapers",      // Here is where you must manipulate subfolders
	"\\good_wallpapers",
	"\\amazing_wallpapers"
});
```
2. Also, change the chanceToSubfolders to match whatever chances you'd like. The last number must be 1.0. The values must be between 0 and 1. 0.25 means 25%, 0.6 means 35% because the first 25% were already taken and thus the third subfolder will have 100% - (35% + 25%), or 40%. If you want the same chance for every subfolder, do increments of 1/subfolderCount and keep the last as 1.0.
```java
private static final List<Double> chanceToSubfolders = Arrays.asList(new Double[]{
			0.25,     // These values must be between 0 and 1
			0.6,      // All numbers must be in order, and it reflects directly the chance of the respective subfolder
			1.0       // The last number must always be 1.0, otherwise code will break given the right circumstances
	});		          // Also the number of elements in this list must always be equal to the number of subfolders
```
