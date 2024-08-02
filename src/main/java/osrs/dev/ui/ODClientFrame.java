package osrs.dev.ui;

import osrs.dev.Main;
import osrs.dev.client.Loader;
import osrs.dev.util.ClientManager;
import osrs.dev.util.ImageUtil;
import osrs.dev.util.ThreadPool;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ODClientFrame extends JFrame {

    private final JTabbedPane tabbedPane;

    public ODClientFrame() {
        setTitle("[ODClient] An Example OSRS Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        BufferedImage icon = ImageUtil.loadImageResource(ODClientFrame.class, "pixal_bot.png");
        setIconImage(icon);

        buildMenuBar();

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new TabChangeListener());
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void buildMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JLabel addTabButton = new JLabel("Add Tab");
        addTabButton.setForeground(Color.LIGHT_GRAY);
        addTabButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        addTabButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addTabButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addNewTab("Tab " + (tabbedPane.getTabCount() + 1), Main.getLoader());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                addTabButton.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                addTabButton.setForeground(Color.LIGHT_GRAY);
            }
        });
        menuBar.add(addTabButton);
        setJMenuBar(menuBar);
    }

    private void addNewTab(String title, Loader loader) {

        ClientContainer panel = new ClientContainer(loader);

        JLabel closeButton = new JLabel("x");
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.setForeground(Color.BLACK);
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = tabbedPane.indexOfComponent(panel);
                if (index != -1) {
                    ClientContainer container = (ClientContainer) tabbedPane.getComponentAt(index);
                    ThreadPool.submit(container::shutdown);
                    tabbedPane.remove(index);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.BLACK);
            }
        });

        JPanel tabHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabHeader.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        tabHeader.add(titleLabel);
        tabHeader.add(closeButton);

        tabbedPane.addTab(title, panel);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabHeader);
    }

    private static class TabChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
            int index = sourceTabbedPane.getSelectedIndex();
            if (index != -1) {
                Component selectedComponent = sourceTabbedPane.getComponentAt(index);
                if (selectedComponent instanceof ClientContainer) {
                    ClientContainer clientContainer = (ClientContainer) selectedComponent;
                    // Perform the desired action with clientContainer
                    handleClientContainer(clientContainer);
                }
            }
            else
            {
                ClientManager.setCurrentClient(null);
                System.out.println("Swapped to: null");
            }
        }

        private void handleClientContainer(ClientContainer clientContainer)
        {
            clientContainer.swapToFront();
        }
    }
}
