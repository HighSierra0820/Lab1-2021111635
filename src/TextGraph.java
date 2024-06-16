import java.io.*;
import java.util.*;

public class TextGraph {
    private Map<String, Map<String, Integer>> graph; // 存储出边及权重
    private Map<String, Set<String>> inboundGraph; // 存储入边

    public TextGraph() {
        graph = new HashMap<>();
        inboundGraph = new HashMap<>();
    }

    // 读取文本文件并构建有向图
    public void buildGraph(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String previousWord = null; // 用于存储上一行最后一个个单词
            while ((line = br.readLine()) != null) {
                //提取单词
                String[] words = line.toLowerCase().replaceAll("[^a-z\\s]", "").split("\\s+");
                for (int i = 0; i < words.length - 1; i++) {
                    String currentWord = words[i];
                    String nextWord = words[i + 1];
                    updateGraph(currentWord, nextWord);
                }
                if (previousWord != null) {
                    updateGraph(previousWord, words[0]);
                }
                previousWord = words[words.length - 1];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 更新有向图和权重
    private void updateGraph(String from, String to) {
        graph.computeIfAbsent(from, k -> new HashMap<>());
        graph.computeIfAbsent(to, k -> new HashMap<>());
        Map<String, Integer> neighbors = graph.get(from);//获取邻接节点映射
        neighbors.put(to, neighbors.getOrDefault(to, 0) + 1); // 增加权重
        // 更新入边信息
        inboundGraph.computeIfAbsent(from, k -> new HashSet<>());
        inboundGraph.computeIfAbsent(to, k -> new HashSet<>());
        Set<String> inboundNeighbors = inboundGraph.get(to);
        inboundNeighbors.add(from);
    }

    // 查询桥接词
    public String queryBridgeWords(String word1, String word2) {
        // 检查 word1 和 word2 是否在图中
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        // 获取 word1 和 word2 的邻接映射,考虑单向性
        Set<String> word1Outbound = graph.get(word1).keySet();
        Set<String> word2Inbound = inboundGraph.get(word2);

        // 找出 word1 和 word2 的共同邻接词
        Set<String> bridgeWords = new HashSet<>(word1Outbound);
        bridgeWords.retainAll(word2Inbound);

        // 根据结果构建输出
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridgeWords);
        }
    }

    // 根据bridge word生成新文本
    public String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().split("\\s+");
        StringBuilder newText = new StringBuilder();
        Random rand = new Random();

        // 遍历输入文本的单词，查找bridge words并生成新文本
        for (int i = 0; i < words.length - 1; i++) {
            String currentWord = words[i];
            String nextWord = words[i + 1];

            // 获取当前单词的出边邻接词
            Map<String, Integer> currentWordAdj = graph.getOrDefault(currentWord, new HashMap<>());
            // 获取下一个单词的入边邻接词
            Set<String> nextWordInbound = inboundGraph.getOrDefault(nextWord, new HashSet<>());

            // 查找共同的邻接词作为bridge words
            Set<String> bridgeWords = new HashSet<>(currentWordAdj.keySet());
            bridgeWords.retainAll(nextWordInbound);

            if (!bridgeWords.isEmpty()) {
                // 如果存在bridge words，随机选择一个插入到新文本中
                String bridgeWord = bridgeWords.toArray(new String[0])[rand.nextInt(bridgeWords.size())];
                newText.append(currentWord).append(" ").append(bridgeWord).append(" ");
            } else {
                // 添加原始单词对，如果没有bridge word或有bridge word已经插入
                newText.append(currentWord).append(" ");
            }
        }
        // 添加最后一个单词
        newText.append(words[words.length - 1]);

        return newText.toString().trim();
    }

    // 计算两个单词之间的最短路径
    public String calcShortestPath(String word1, String word2) {
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No path exists between " + word1 + " and " + word2 + ".";
        }

        Queue<String> queue = new LinkedList<>();//队列
        Set<String> visited = new HashSet<>();//已访问节点集合
        Map<String, String> prev = new HashMap<>();//前驱节点映射
        Map<String, Integer> distance = new HashMap<>();//距离映射

        // 初始化
        queue.offer(word1);
        visited.add(word1);
        distance.put(word1, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            for (Map.Entry<String, Integer> neighborEntry : graph.get(current).entrySet()) {
                String neighbor = neighborEntry.getKey();
                int weight = neighborEntry.getValue();
                int distanceThroughCurrent = distance.getOrDefault(current, 0) + 1;
                if (!visited.contains(neighbor) && (!distance.containsKey(neighbor) || distanceThroughCurrent < distance.get(neighbor))) {
                    visited.add(neighbor);
                    prev.put(neighbor, current);
                    distance.put(neighbor, distanceThroughCurrent);
                    queue.offer(neighbor);
                }
            }
        }
        // 检查是否到达目标节点
        if (distance.containsKey(word2)) {
            return reconstructPath(prev, word1, word2) + " with length " + distance.get(word2) + ".";
        }
        return "No path exists between " + word1 + " and " + word2 + ".";
    }

    // 根据 prev 映射重构路径
    private String reconstructPath(Map<String, String> prev, String start, String end) {
        LinkedList<String> path = new LinkedList<>();
        for (String current = end; current != null; current = prev.get(current)) {
            path.addFirst(current);
        }
        return "Shortest path from " + start + " to " + end + " is: " + path ;
    }

    // 随机游走
    public void randomWalk(Scanner scanner) {
        Map<String, Integer> edgeCount = new HashMap<>(); // 记录边的出现次数
        Random rand = new Random();
        // 随机选择起始节点
        List<String> nodes = new ArrayList<>(graph.keySet());
        String startNode = nodes.get(rand.nextInt(nodes.size()));
        // 记录游走路径
        StringBuilder walk = new StringBuilder(startNode);

        while (true) {
            if (!graph.containsKey(startNode) || graph.get(startNode).isEmpty()) {
                // 如果当前节点没有出边，结束游走
                break;
            }

            // 选择随机邻接节点
            Map<String, Integer> neighbors = graph.get(startNode);
            List<String> neighborKeys = new ArrayList<>(neighbors.keySet());
            String nextNode = neighborKeys.get(rand.nextInt(neighborKeys.size()));

            // 记录游走路径
            walk.append(" > ").append(nextNode);
            startNode = nextNode; // 移动到下一个节点

            // 输出当前路径
            System.out.println("Current Path: " + walk.toString());

            // 更新边的计数
            String edge = startNode + " -> " + nextNode;
            if(edgeCount.getOrDefault(edge, 0) + 1 == 2){
                break;
            } else{
                edgeCount.put(edge, edgeCount.getOrDefault(edge, 0) + 1);
            }

            // 询问用户是否继续
            while (true) {
                System.out.print("Do you want to continue (yes/no)? ");
                String userInput = scanner.nextLine().trim().toLowerCase();
                if (userInput.equals("yes")) {
                    break; // 用户选择继续
                } else if (userInput.equals("no")) {
                    System.out.println("Random walk stopped.");
                    break; // 用户选择停止
                } else {
                    System.out.println("Invalid input, please enter 'yes' or 'no'.");
                }
            }
        }
    }

    // 生成代表图形的 DOT 语言字符串
    public String generateGraphvizDotString() {
        StringBuilder dotBuilder = new StringBuilder("digraph G {\n");
        dotBuilder.append("    rankdir=LR;\n"); // 设置布局方向
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            String node = entry.getKey();
            for (Map.Entry<String, Integer> neighborEntry : entry.getValue().entrySet()) {
                String neighbor = neighborEntry.getKey();
                dotBuilder.append("    \"").append(node).append("\" -> \"").append(neighbor)
                        .append("\" [label=\"").append(neighborEntry.getValue()).append("\"];\n");
            }
        }
        dotBuilder.append("}");
        return dotBuilder.toString();
    }

    // 使用 Graphviz 生成图片
    public void showDirectedGraph(Scanner scanner) {
        System.out.print("Enter the output image file path (e.g., C:\\path\\to\\image.png): ");
        String outputPath = scanner.nextLine();
        String dotString = generateGraphvizDotString();
        try {
            // 将 DOT 字符串写入临时文件
            File tempDotFile = File.createTempFile("graph", ".dot");
            try (PrintWriter writer = new PrintWriter(tempDotFile, "UTF-8")) {
                writer.println(dotString);
            }

            // 调用 Graphviz 的 dot 命令生成图片
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", tempDotFile.getAbsolutePath(), "-o", outputPath);
            Process process = processBuilder.start();
            process.waitFor();

            // 删除临时 DOT 文件
            tempDotFile.delete();

            System.out.println("Graph image generated at: " + outputPath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TextGraph textGraph = new TextGraph();
        System.out.println("Welcome to the Text Graph Application!");

        // 读取文本文件并构建有向图
        System.out.print("Enter the file path: ");
        String filePath = scanner.nextLine();
        textGraph.buildGraph(filePath);

        boolean running = true;
        while (running) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Query Bridge Words");
            System.out.println("2. Generate New Text");
            System.out.println("3. Calculate Shortest Path");
            System.out.println("4. Perform Random Walk");
            System.out.println("5. Show Directed Graph");
            System.out.println("6. Exit");

            System.out.print("Enter your choice (1-6): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter word1: ");
                    String word1 = scanner.nextLine();
                    System.out.print("Enter word2: ");
                    String word2 = scanner.nextLine();
                    System.out.println(textGraph.queryBridgeWords(word1, word2));
                    break;
                case "2":
                    System.out.print("Enter input text: ");
                    String inputText = scanner.nextLine();
                    System.out.println(textGraph.generateNewText(inputText));
                    break;
                case "3":
                    System.out.print("Enter start word: ");
                    String startWord = scanner.nextLine();
                    System.out.print("Enter end word: ");
                    String endWord = scanner.nextLine();
                    System.out.println(textGraph.calcShortestPath(startWord, endWord));
                    break;
                case "4":
                    textGraph.randomWalk(scanner);
                    break;
                case "5":
                    textGraph.showDirectedGraph(scanner);
                    break;
                case "6":
                    running = false;
                    System.out.println("Exiting the application...");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }

        scanner.close();
    }
}