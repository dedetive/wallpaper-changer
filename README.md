# Wallpaper Changer
A program that changes your wallpaper, with some tweaks.

This was made by me because I'm not allowed to have whatever wallpaper I wish to have because of strict parenting, so it's centered around having a default wallpaper that i'm allowed to use and those within the good_wallpapers folder are those i'm not allowed to use.
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
	private static final String subfolder1 = "\\decent_wallpapers";
	private static final String subfolder2 = "\\good_wallpapers";
	private static final String subfolder3 = "\\amazing_wallpapers";
	private static final String defaultWallpaperPath = "C:\\Users\\ded\\Pictures\\wallpapers\\rabbito.jpg";
	private static final double chanceToSubfolder1 = 0.2; // These must be between 0 and 1
	private static final double chanceToSubfolder2 = 0.6; // This must be higher than chanceToSubfolder1
	private static int changeIntervalMS = 600000; // Change wallpaper every ten minutes (600000 ms)
```

Paths must have double backslashes.

wallpaperFolder is the path to the folder that contains subfolders with the images.
subfolder1 is the first subfolder within wallpaperFolder.
subfolder2 is the second subfolder.
subfolder3 is the third subfolder.
defaultWallpaperPath is the full path to where the default picture is located. It must be a file.
chanceToSubfolder1 is the chance for the random number generator to pick a file from subfolder1.
chanceToSubfolder2 is the chance for the random number generator to pick a file from subfolder2. This means there's 20% to be a file in subfolder1, 40% in subfolder2 and the remaining 40% in subfolder3.
changeIntervalMS is the default interval in milliseconds to change the wallpaper. Ctrl + Alt + 2 and 8 may affect this.
