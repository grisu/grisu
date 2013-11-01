package grisu.model.utils;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Maps;
import grisu.model.FileManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.zeroturnaround.zip.FileSource;
import org.zeroturnaround.zip.ZipEntrySource;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 2/09/13
 * Time: 6:21 PM
 */
public class ZipUtils {

    public static void main(String[] args) {

        Map<String, Set<String>> entries = Maps.newHashMap();
        Set<String> sources = Sets.newHashSet();
        sources.add("/home/markus/Desktop/testjob");
        sources.add("/home/markus/submit.grisu");
        entries.put("", sources);

        createZipFile(new File("/home/markus/zipFile.zip"), entries);

    }

    public static File createTempZipFile(Map<String, Set<String>> entries) {
        try {
            File temp = File.createTempFile("grisu", "upload");
            createZipFile(temp, entries);
            return temp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createZipFile(File zipFile, Map<String, Set<String>> entries) {

        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        List<ZipEntrySource> zess = Lists.newArrayList();
        for (String path : entries.keySet()) {

            Set<String> sources = entries.get(path);
            for (String source : sources) {
                File sourceFile = FileManager.getFileFromUriOrPath(source);

                if (sourceFile.isDirectory()) {

                    final Collection<File> allFiles = FileUtils.listFiles(sourceFile, null,
                            true);
                    final String basePath = sourceFile.getPath();



                    for (File f : allFiles) {

                     final String filePath = f.getPath();
                    final String deltaPathTemp = path + "/"
                            + filePath.substring(basePath.length());

                    String deltaPath;
                    if (deltaPathTemp.startsWith("/") || deltaPathTemp.startsWith("\\")) {
                        deltaPath = deltaPathTemp.substring(1);
                    } else {
                        deltaPath = deltaPathTemp;
                    }

                    deltaPath = deltaPath.replace('\\', '/');
                    deltaPath = deltaPath.replace("/./", "/");

                    // try {
                    // deltaPath = URLEncoder.encode(deltaPath, "UTF-8");
                    deltaPath = deltaPath.replaceAll("\\s", "%20");

                        System.out.println("PATH: "+deltaPath);


                        ZipEntrySource zes = null;
                        if (StringUtils.isBlank(path)) {
                            zes = new FileSource(sourceFile.getName() + deltaPath, f);
                        } else {
                            zes = new FileSource(path + sourceFile.getName() + "/" + deltaPath, f);
                        }

                        zess.add(zes);
                    }


                } else {
                    ZipEntrySource zes = null;
                    if (StringUtils.isBlank(path)) {
                        zes = new FileSource(sourceFile.getName(), sourceFile);
                    } else {
                        zes = new FileSource(path, sourceFile);
                    }

                    zess.add(zes);
                }
            }

        }

        ZipUtil.pack(zess.toArray(new ZipEntrySource[]{}), zipFile);

    }



}
