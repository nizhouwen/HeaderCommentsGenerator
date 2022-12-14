import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.logging.Handler;

public class HeaderCommentsGenerator extends AnAction {

    private static int count = 0;

    public static void main(String[] args) {
        handNotes();

    }

    public static void handNotes() {
        final JFrame frame = new JFrame("HeaderCommentsGenerator 1.2");

        JPanel contentPane = (JPanel) frame.getContentPane();

        JPanel centerPane = new JPanel(new BorderLayout(10, 10));

        centerPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel pathPane = new JPanel(new BorderLayout());

        final JTextField txtPath = new JTextField();

        txtPath.setText("Please select your file or path.");

        pathPane.add(txtPath, BorderLayout.CENTER);

        JButton btnSelectPath = new JButton("Browser...");

        btnSelectPath.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();

                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

                int returnVal = chooser.showOpenDialog(frame);

                if (returnVal == JFileChooser.APPROVE_OPTION) {

                    txtPath.setText(chooser.getSelectedFile().getAbsolutePath());

                }

            }

        });

        btnSelectPath.setMnemonic('B');

        pathPane.add(btnSelectPath, BorderLayout.EAST);

        centerPane.add(pathPane, BorderLayout.NORTH);

        final JTextArea txtComments = new JTextArea();

        txtComments.setText("/*\n" +

                " * add note here\n" +

                " */");

        centerPane.add(new JScrollPane(txtComments), BorderLayout.CENTER);

        contentPane.add(centerPane, BorderLayout.CENTER);

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnOK = new JButton("Generate!");

        btnOK.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String path = txtPath.getText();

                File file = new File(path);

                if (!file.exists()) {

                    JOptionPane.showMessageDialog(frame,

                            "Path '" + path + "' not exist.",

                            "Error",

                            JOptionPane.ERROR_MESSAGE);

                } else {

                    commentFile(file, txtComments.getText());

                    JOptionPane.showMessageDialog(frame,

                            "Finish, total " + count + " files are processed.",

                            "Information",

                            JOptionPane.INFORMATION_MESSAGE);

                }

            }

        });

        btnOK.setMnemonic('G');

        JButton btnClose = new JButton("Close");

        btnClose.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                System.exit(0);

            }

        });

        btnClose.setMnemonic('C');

        buttonPane.add(btnOK);

        buttonPane.add(btnClose);

        contentPane.add(buttonPane, BorderLayout.SOUTH);

        frame.setSize(500, 300);

        frame.show();

    }

    private static void commentFile(File file, String comments) {

        if (file != null && file.exists()) {

            if (file.isDirectory()) {

                String[] children = file.list();

                for (int i = 0; i < children.length; i++) {

                    File child = new File(file.getPath() + System.getProperty("file.separator") + children[i]);

                    commentFile(child, comments);

                }

            } else {

                if (file.getName().toLowerCase().endsWith(".java")) {

                    System.out.println(file.getName());

                    count++;

                    try {

                        RandomAccessFile raFile = new RandomAccessFile(file, "rw");

                        byte[] content = new byte[(int) raFile.length()];

                        raFile.readFully(content);

                        String all = new String(content);

                        all = all.trim();

                        while (all.startsWith("\n")) {

                            all = all.substring(1);

                        }

                        if (all.indexOf("package") != -1) {

                            all = all.substring(all.indexOf("package"));

                        }

                        if (all.indexOf("import") != -1) {

                            all = all.substring(all.indexOf("package"));

                        }

                        all = comments + "\n" + all;

                        raFile.close();

                        FileWriter writer = new FileWriter(file);

                        writer.write(all);

                        writer.close();

                    } catch (Exception ex) {

                        ex.printStackTrace();

                    }

                }

            }

        }

    }

    @Override
    public void actionPerformed(AnActionEvent e) {
//        Project project = e.getProject();
//        Messages.showMessageDialog(project, "this is message content", "this message title", Messages.getInformationIcon());
        handNotes();
    }
}
