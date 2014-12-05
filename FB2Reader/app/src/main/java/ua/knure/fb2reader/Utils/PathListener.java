package ua.knure.fb2reader.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;

import ua.knure.fb2reader.DataAccess.DAO;

/**
 * @author evilcorp
 * @version 1.0
 */
public class PathListener implements DbxFileSystem.PathListener {
	public static final String PATH = Environment.getExternalStorageDirectory() + "/.fb2reader/";
	
    @Override
    public void onPathChange(DbxFileSystem dbxFileSystem, DbxPath dbxPath, Mode mode) {
        Log.d("myLogs", "path was changed");
        try {
			downloadFiles(dbxFileSystem, dbxPath);
			uploadFiles(dbxFileSystem);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void downloadFiles(DbxFileSystem dbxFs, DbxPath dbxPath) throws IOException {
		Log.d("myLogs", "download");
		List<DbxFileInfo> list = dbxFs.listFolder(dbxPath);
		DbxFile testFile;
		File pathBooks = new File(PATH);
		if (!pathBooks.exists()) {
			pathBooks.mkdir();
		}
		File[] files = getLocalFiles();
		OutputStream fOut = null;
		for (DbxFileInfo info : list) {
			//Log.d("myLogs", info.path.getName());
			if (isInLocal(info.path.getName(), files)) {
				continue;
			}
            Log.d("myLogs", info.path.getName());
			testFile = dbxFs.open(new DbxPath(info.path.getName()));
			String content = testFile.readString();
			File f = new File(pathBooks, info.path.getName());
			f.createNewFile();
			fOut = new FileOutputStream(f);
			fOut.write(content.getBytes());
			fOut.flush();
			fOut.close();
			testFile.close();
		}
	}
    

	private static File[] getLocalFiles() {
		return new File(PATH).listFiles();
	}
	
	private static boolean isInLocal(String name, File[] files) {
		for (File f : files) {
			if (f.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isInBox(String name, List<DbxFileInfo> files) {
		for (DbxFileInfo f : files) {
			if (f.path.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public static void uploadFiles(DbxFileSystem dbxFs) throws InvalidPathException, IOException {
		Log.d("myLogs", "upload");
		DbxFile testFile = null;
		for (File f : getLocalFiles()) {
			if (isInBox(f.getName(), getBoxFiles(dbxFs))) {
				continue;
			}
            Log.d("myLogs", f.getName());
			testFile = dbxFs.create(new DbxPath(f.getName()));
			testFile.close();
		}
	}
	
	private static List<DbxFileInfo> getBoxFiles(DbxFileSystem dbxFs) throws DbxException {
		DbxPath dbPath = new DbxPath("/");
		List<DbxFileInfo> list = dbxFs.listFolder(dbPath);
		return list;
	}

    public static void addBooksToDB(DAO dao, String email) {
        File[] books = getLocalFiles();
        List<String> bookNames = new ArrayList<>();
        for(File book : books) {
            bookNames.add(book.getName());
        }
        dao.addBooks(bookNames, email);
    }
}
