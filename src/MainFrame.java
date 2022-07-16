import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private JButton btnSearch;
    private JPanel mainPanel;
    private JLabel message;
    private JLabel filesMoved;
    private JLabel foldersCreated;
    private JLabel diretorio;

    public MainFrame() {
        setContentPane(mainPanel);
        setTitle("File Organizer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        message.setForeground(Color.RED);
        message.setText("WAITING");
        setVisible(true);

        btnSearch.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            int response = fileChooser.showOpenDialog(null);
            AtomicInteger qtdArquivosMovidos = new AtomicInteger();
            diretorio.setText("");
            filesMoved.setText("");
            foldersCreated.setText("");

            if(response == JFileChooser.APPROVE_OPTION) {

                if(fileChooser.getSelectedFile().isFile()) {
                    setSize(300, 150);
                    message.setText("NOT A FOLDER, TRY AGAIN.");
                    return;
                }

                String path = fileChooser.getSelectedFile().getAbsolutePath();

                try {
                    List<String> fileTypes = new ArrayList<>();
                    List<File> files = Files.list(Paths.get(path))
                            .map(Path::toFile)
                            .filter(File::isFile)
                            .collect(Collectors.toList());

                    files.forEach(file -> {
                        String extension = getExtension(file.getName());

                        if(extension == null) {
                            extension = "none";
                        }

                        String pathSalvar = (file.getParent() + "\\" + extension + "\\").replace("\\", "/");
                        File theDir = new File(pathSalvar);
                        if(!theDir.exists()){
                            theDir.mkdirs();
                        }
                        boolean isMoved = file.renameTo(new File(pathSalvar + file.getName()));

                        if(isMoved) {
                            qtdArquivosMovidos.getAndIncrement();
                        }

                        if(!fileTypes.contains(extension)) {
                            fileTypes.add(extension);
                        }
                    });

                    setSize(260, 200);
                    diretorio.setText("Path: " + path);
                    filesMoved.setText("Files Moved: " + qtdArquivosMovidos);
                    foldersCreated.setText("Folders Created: " + fileTypes.toString()
                            .replace("[","")
                            .replace("]",""));
                    message.setForeground(Color.GREEN);
                    message.setText("DONE !!!");

                } catch (IOException f) {
                    message.setText("ERROR");
                }

            }

        });
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");  // This line gives Windows Theme
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        new MainFrame();
    }

    public static String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        }
        return null;
    }


}
