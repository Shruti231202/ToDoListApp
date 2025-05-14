import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

// Task class (Data class)
class Task implements Serializable {
    private String name;
    private boolean isComplete;

    public Task(String name) {
        this.name = name;
        this.isComplete = false;
    }

    public String getName() {
        return name;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void markAsComplete() {
        isComplete = true;
    }

    @Override
    public String toString() {
        return (isComplete ? "[Done] " : "[Pending] ") + name;
    }
}

// TaskManager class (Handles the logic and file I/O)
class TaskManager {
    private ArrayList<Task> tasks;
    private final String FILE_NAME = "tasks.dat";

    public TaskManager() {
        tasks = loadTasks();
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void addTask(String name) {
        tasks.add(new Task(name));
        saveTasks();
    }

    public void completeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markAsComplete();
            saveTasks();
        }
    }

    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            saveTasks();
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(tasks);
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    private ArrayList<Task> loadTasks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (ArrayList<Task>) in.readObject();
        } catch (Exception e) {
            return new ArrayList<>(); // If no file found or error, return empty list
        }
    }
}

// Main class (Handles the UI part using Swing)
public class ToDoListApp {
    private TaskManager manager;
    private JFrame frame;
    private DefaultListModel<String> listModel;
    private JList<String> taskList;

    public ToDoListApp() {
        manager = new TaskManager();
        frame = new JFrame("To-Do List");
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);

        loadTasksToUI();

        JButton addButton = new JButton("Add Task");
        JButton completeButton = new JButton("Mark as Complete");
        JButton deleteButton = new JButton("Delete Task");

        JTextField taskInput = new JTextField(20);

        addButton.addActionListener(e -> {
            String text = taskInput.getText().trim();
            if (!text.isEmpty()) {
                manager.addTask(text);
                taskInput.setText("");
                loadTasksToUI();
            }
        });

        completeButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                manager.completeTask(selectedIndex);
                loadTasksToUI();
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                manager.deleteTask(selectedIndex);
                loadTasksToUI();
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(taskInput);
        inputPanel.add(addButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(taskList), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void loadTasksToUI() {
        listModel.clear();
        for (Task task : manager.getTasks()) {
            listModel.addElement(task.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListApp::new);
    }
}
