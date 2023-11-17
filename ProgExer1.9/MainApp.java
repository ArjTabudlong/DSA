import java.util.*;
import java.io.*;

class Node {
    String softwareName;
    String version;
    int quantity;
    int price;
    // int position;

    Node left, right;

    public Node(String softwareName, String version, int quantity, int price) {
        this.softwareName = softwareName;
        this.version = version;
        this.quantity = quantity;
        this.price = price;
        // this.position = position;

        left = right = null;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getName() {
        return this.softwareName;
    }

    public String getVersion() {
        return this.version;
    }

    public int getPrice() {
        return this.price;
    }

}

class BinaryTree {
    Node root;

    void insert(String softwareName, String version, int quantity, int price) {
        root = insertRec(root, softwareName, version, quantity, price);
    }

    Node insertRec(Node root, String softwareName, String version, int quantity, int price) {
        if (root == null) {
            root = new Node(softwareName, version, quantity, price);
            return root;
        }
        if (softwareName.compareTo(root.softwareName) < 0)
            root.left = insertRec(root.left, softwareName, version, quantity, price);
        else if (softwareName.compareTo(root.softwareName) > 0)
            root.right = insertRec(root.right, softwareName, version, quantity, price);
        return root;
    }

    public Node search(String softwareName) {
        return searchRec(root, softwareName);
    }

    private Node searchRec(Node root, String softwareName) {
        if (root == null || root.softwareName.equals(softwareName))
            return root;

        if (root.softwareName.compareTo(softwareName) > 0)
            return searchRec(root.left, softwareName);

        return searchRec(root.right, softwareName);
    }

    List<String> preorder() {
        List<String> result = new ArrayList<>();
        preorderRec(root, result);
        return result;
    }

    void preorderRec(Node root, List<String> result) {
        if (root != null) {
            result.add(root.softwareName + ", " + root.version + ", " + root.quantity + ", " + root.price);
            preorderRec(root.left, result);
            preorderRec(root.right, result);
        }
    }

    public List<Node> preorderUpdate() {
        List<Node> nodes = new ArrayList<>();
        preorder(root, nodes);
        return nodes;
    }

    private void preorder(Node node, List<Node> nodes) {
        if (node == null) {
            return;
        }
        nodes.add(node);
        preorder(node.left, nodes);
        preorder(node.right, nodes);
    }

}

public class MainApp {
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        boolean cont = true;

        BinaryTree tree = new BinaryTree();
        File softwares = new File("src/software.txt");
        Scanner fileReader;

        try {
            fileReader = new Scanner(softwares);

            while (fileReader.hasNextLine()) {
                String softwareName = fileReader.nextLine();
                String version = fileReader.nextLine();
                int quantity = Integer.parseInt(fileReader.nextLine());
                int price = Integer.parseInt(fileReader.nextLine());

                tree.insert(softwareName, version, quantity, price);
            }

        } catch (IOException e) {
            System.out.println("Error: File not found.");
        }

        while (cont) {
            System.out.println("-------------------- Software Store --------------------");
            System.out.println("Type the letter of the option you want to choose:");
            System.out.println("    A: Show available software");
            System.out.println("    B: Add software");
            System.out.println("    C: Sell software");
            System.out.println("    D: Exit program");
            System.out.print("========================================================\nChoice: ");

            char choice;
            while (true) {
                choice = Character.toUpperCase(in.nextLine().charAt(0));
                if (choice == 'A' || choice == 'B' || choice == 'C' || choice == 'D') {
                    break;
                } else {
                    System.out.print("Invalid choice. Please try again.\n\nChoice: ");
                }
            }
            System.out.println("========================================================");
            switch (choice) {
                case 'A':
                    System.out.println("Available software:");
                    List<String> softwareList = tree.preorder();
                    System.out.printf("%-30s %-20s %-10s %-10s\n", "Name:", "Version:", "Quantity:", "Price:");
                    for (String software : softwareList) {
                        String[] details = software.split(",");
                        System.out.printf("%-30s %-20s %-10s %-10s\n",
                                details[0],
                                details[1],
                                details[2],
                                details[3]);
                    }
                    System.out.println("========================================================");
                    break;
                case 'B':
                    System.out.println("Write the details of the software you want to add:");
                    addSoftware(tree, in);

                    System.out.println("\nSoftware added successfully.");
                    break;
                case 'C':

                    sellSoftware(tree, in);
                    break;
                case 'D':
                    cont = false;
                    break;
                default:
                    System.out.println("Invalid choice");
            }

        }

    }

    public static void addSoftware(BinaryTree tree, Scanner in) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("src/software.txt", true);

            System.out.print("Name: ");
            String softwareName = in.nextLine();
            writer.write(System.lineSeparator() + softwareName + "\n");
            System.out.print("Version: ");
            String version = in.nextLine();
            writer.write(version + "\n");
            System.out.print("Quantity: ");
            int quantity = in.nextInt();
            writer.write(quantity + "\n");
            System.out.print("Price: ");
            int price = in.nextInt();
            writer.write(price + "\n");
            in.nextLine();
            tree.insert(softwareName, version, quantity, price);
        } catch (Exception e) {
            System.out.println("Error: File not added.");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Error: Could not close file.");
                }
            }

        }
        updateList(tree, new File("src/software.txt"));

    }

    public static void sellSoftware(BinaryTree tree, Scanner in) {
        System.out.print("Enter the software name to sell: ");
        String softwareName = in.nextLine();
        System.out.print("Copies sold: ");
        int copiesSold = in.nextInt();
        in.nextLine();
        Node softwareNode = tree.search(softwareName);

        if (softwareNode != null) {
            if (softwareNode.getQuantity() > 0) {
                softwareNode.setQuantity(softwareNode.getQuantity() - copiesSold);
                System.out.println("\nSoftware sold successfully. Remaining quantity: " + softwareNode.getQuantity());

            } else {
                System.out.println("Software is out of stock.");
            }
        } else {
            System.out.println("Software not available.");
        }
        updateList(tree, new File("src/software.txt"));
    }

    public static void updateList(BinaryTree tree, File file) {
        try (FileWriter writer = new FileWriter(file, false)) { // false to overwrite the file
            List<Node> softwareList = tree.preorderUpdate();
            for (Node software : softwareList) {
                if (software.getQuantity() > 0) {
                    writer.write(software.getName() + System.lineSeparator());
                    writer.write(software.getVersion() + System.lineSeparator());
                    writer.write(software.getQuantity() + System.lineSeparator());
                    writer.write(software.getPrice() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Error: Could not write to file.");
        }

    }
}
