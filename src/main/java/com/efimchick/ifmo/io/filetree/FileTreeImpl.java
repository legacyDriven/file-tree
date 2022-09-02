package com.efimchick.ifmo.io.filetree;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FileTreeImpl implements FileTree {
    String bytes = " bytes";

    @Override
    public Optional<String> tree(Path path) {
        File file = new File(String.valueOf(path));
        if (!file.exists()) return Optional.empty();
        if (file.isFile()) return Optional.of(file.getName() + " " + file.length() + bytes);
        if (file.isDirectory()) return Optional.of(directoryTree(file, new ArrayList<>()));
        return Optional.empty();
    }

    private String directoryTree(File folder, List<Boolean> lastFolders) {
        StringBuilder result = new StringBuilder();
        if (lastFolders.size() != 0)
            result.append(!(lastFolders.get(lastFolders.size() - 1)) ? "├─ " : "└─ ");
        result.append(folder.getName()).append(" ").append(folderSize(folder));

        File[] files = folder.listFiles();

        if(files != null) {
            int count = files.length;
            files = sortFiles(files);
            for (int i = 0; i < count; i++) {
                result.append("\n");
                for (Boolean lastFolder : lastFolders) {
                    if (lastFolder) {
                        result.append("   ");
                    } else {
                        result.append("│  ");
                    }
                }
                if (files[i].isFile()) {
                    result.append(i + 1 == count ? "└" : "├")
                            .append("─ ")
                            .append(files[i].getName())
                            .append(" ")
                            .append(files[i].length()).append(bytes);
                } else {
                    lastFolders.add(i + 1 == count);
                    result.append(directoryTree(files[i], lastFolders));
                    lastFolders.remove(lastFolders.size() - 1);
                }}}
        return result.toString();
    }

    private String folderSize(File folder) {
        return getFolderSize(folder) + bytes;
    }

    private long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        if(files != null){
        for (File file : files) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += getFolderSize(file);
                }}}
        return length;
    }

    private File[] sortFiles(File[] folder) {
        folder = Arrays.stream(folder).sorted(Comparator.comparing(x -> x.getName().toLowerCase())).toArray(File[]::new);
        List<File> result = new ArrayList<>();
        result.addAll(Arrays.stream(folder).filter(File::isDirectory).collect(Collectors.toList()));
        result.addAll(Arrays.stream(folder).filter(File::isFile).collect(Collectors.toList()));
        return result.toArray(new File[0]);
    }
}
