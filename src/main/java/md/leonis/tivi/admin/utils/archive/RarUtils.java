package md.leonis.tivi.admin.utils.archive;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import lombok.SneakyThrows;
import md.leonis.tivi.admin.model.ArchiveEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RarUtils {

    /*public static void extractArchive(String archive, String destination) {
        if (archive == null || destination == null) {
            throw new RuntimeException("archive and destination must me set");
        }
        File arch = new File(archive);
        if (!arch.exists()) {
            throw new RuntimeException("the archive does not exit: " + archive);
        }
        File dest = new File(destination);
        if (!dest.exists() || !dest.isDirectory()) {
            throw new RuntimeException(
                    "the destination must exist and point to a directory: "
                            + destination);
        }
        extractArchive(arch, dest);
    }*/

    /*public static void extractArchive(File archive, File destination) {
        Archive arch = null;
        try {
            arch = new Archive(archive);
        } catch (RarException | IOException e) {
            e.printStackTrace();
        }
        if (arch != null) {
            if (arch.isEncrypted()) {
                throw new RuntimeException("archive is encrypted cannot extreact");
            }
            FileHeader fh;
            while (true) {
                fh = arch.nextFileHeader();
                if (fh == null) {
                    break;
                }
                if (fh.isEncrypted()) {
                    throw new RuntimeException("file is encrypted cannot extract: "
                            + fh.getFileNameString());
                }
                System.out.println("Extracting: " + fh.getFileNameString());
                try {
                    if (fh.isDirectory()) {
                        createDirectory(fh, destination);
                    } else {
                        File f = createFile(fh, destination);
                        OutputStream stream = new FileOutputStream(f);
                        arch.extractFile(fh, stream);
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RarException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/


    public static void extractArchive(Path sourcePath, Path destPath, String fileName) {
        System.out.println(sourcePath);
        Archive arch = null;
        try {
            arch = new Archive(sourcePath.toFile());
        } catch (RarException | IOException e) {
            e.printStackTrace();
        }
        if (arch != null) {
            if (arch.isEncrypted()) {
                throw new RuntimeException("archive is encrypted cannot extreact");
            }
            FileHeader fh;
            while (true) {
                fh = arch.nextFileHeader();
                if (fh == null) {
                    break;
                }
                if (fh.isEncrypted()) {
                    throw new RuntimeException("file is encrypted cannot extract: "
                            + fh.getFileNameString());
                }
                try {
                    if (fh.isDirectory()) {
                        createDirectory(fh, destPath.toFile());
                    } else {
                        System.out.println("Extracting: " + fh.getFileNameString());
                        File f = createFile(destPath.toFile(), fileName + "." + SevenZipUtils.getExtension(getName(fh)));
                        OutputStream os = new FileOutputStream(f);
                        arch.extractFile(fh, os);
                        os.close();
                    }
                } catch (IOException | RarException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<ArchiveEntry> getRarFileList(File file) {
        List<ArchiveEntry> files = new ArrayList<>();
        Archive arch = null;
        try {
            arch = new Archive(file);
        } catch (RarException | IOException e) {
            e.printStackTrace();
        }
        if (arch != null) {
            if (arch.isEncrypted()) {
                throw new RuntimeException("archive is encrypted cannot extreact");
            }
            FileHeader fh;
            while (true) {
                fh = arch.nextFileHeader();
                if (fh == null) {
                    break;
                }
                if (fh.isEncrypted()) {
                    throw new RuntimeException("file is encrypted cannot extract: "
                            + fh.getFileNameString());
                }
                files.add(new ArchiveEntry(getName(fh), fh.getFileCRC(), fh.getUnpSize()));
            }
        }
        return files;
    }

    private static String getName(FileHeader fh) {
        if (fh.isUnicode()) {
            return fh.getFileNameW();
        } else {
            return fh.getFileNameString();
        }
    }

    @SneakyThrows
    private static File createFile(File destination, String fileName) {
        String name = SevenZipUtils.findFreeFileName(destination.toPath(), fileName, 0).getFileName().toString();
        return makeFile(destination, name);
    }

    /*@SneakyThrows
    private static File createFile(FileHeader fh, File destination) {
        String name = SevenZipUtils.findFreeFileName(destination.toPath(), getName(fh), 0).getFileName().toString();
        return makeFile(destination, name);
    }*/

    private static File makeFile(File destination, String name) throws IOException {
        if (name == null) {
            return null;
        }
        String[] dirs = name.split("\\\\");
        String path = "";
        int size = dirs.length;
        if (size == 1) {
            return new File(destination, name);
        } else if (size > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                path = path + File.separator + dirs[i];
                mkdirs(new File(destination, path));
            }
            path = path + File.separator + dirs[dirs.length - 1];
            File f = new File(destination, path);
            f.createNewFile();
            return f;
        } else {
            return null;
        }
    }

    private static void createDirectory(FileHeader fh, File destination) {
        File f;
        if (fh.isDirectory() && fh.isUnicode()) {
            f = new File(destination, fh.getFileNameW());
            if (!f.exists()) {
                makeDirectory(destination, fh.getFileNameW());
            }
        } else if (fh.isDirectory() && !fh.isUnicode()) {
            f = new File(destination, fh.getFileNameString());
            if (!f.exists()) {
                makeDirectory(destination, fh.getFileNameString());
            }
        }
    }

    private static void makeDirectory(File destination, String fileName) {
        if (fileName == null) {
            return;
        }
        String[] dirs = fileName.split("\\\\");
        String path = "";
        for (String dir : dirs) {
            path = path + File.separator + dir;
            mkdirs(new File(destination, path));
        }

    }

    //TODO separate utils
    private static void mkdirs(Path path) {
        mkdirs(path.toFile());
    }

    @SuppressWarnings("all")
    private static void mkdirs(File file) {
        file.mkdirs();
    }
}
