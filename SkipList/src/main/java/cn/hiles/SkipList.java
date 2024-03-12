package cn.hiles;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Helios
 * Time：2024-03-09 23:56
 */
public class SkipList<K extends Comparable<K>, V> {
    /**
     * 跳表的最大层级
     */
    private static final int MAX_LEVEL = 32;
    /**
     * 数据持久化路径
     */
    private static final String STORE_PATH = "./store";
    /**
     * 跳表的头节点
     */
    private final Node<K, V> header;
    /**
     * 跳表的节点数量
     */
    private int size;
    /**
     * 跳表的当前层级
     */
    private int skipListLevel;

    /**
     * 跳表的构造函数
     */
    public SkipList() {
        header = new Node<>(null, null, MAX_LEVEL);
        size = 0;
        skipListLevel = 0;
    }

    /**
     * 生成一个随机的层级
     *
     * @return 随机的层级
     */
    private static int generateRandomLevel() {
        int level = 0;
        Random random = new Random();
        while (random.nextInt(2) == 1) {
            level++;
        }
        return level;
    }

    /**
     * 创建一个新的节点
     *
     * @param key   键
     * @param value 值
     * @param level 节点的层级
     * @return 新的节点
     */
    private Node<K, V> createNode(K key, V value, int level) {
        return new Node<>(key, value, level);
    }

    /**
     * 跳表中的节点数量
     *
     * @return 节点数量
     */
    public int size() {
        return size;
    }

    /**
     * 向跳表中插入一个键值对，如果跳表中已经存在相同 key 的节点，则更新这个节点的 value
     *
     * @param key   键
     * @param value 值
     * @return 插入成功返回 true，否则返回 false
     */
    public synchronized boolean insert(K key, V value) {
        Node<K, V> current = header;
        ArrayList<Node<K, V>> update = new ArrayList<>(Collections.nCopies(MAX_LEVEL, null));

        for (int i = skipListLevel; i >= 0; i--) {
            while (current.forwards.get(i) != null && current.forwards.get(i).key.compareTo(key) < 0) {
                current = current.forwards.get(i);
            }
            update.set(i, current);
        }

        current = current.forwards.get(0);

        //如果当前节点的key等于要插入的key，则更新value
        if (current != null && current.key.compareTo(key) == 0) {
            current.setValue(value);
            return true;
        }

        int randomLevel = generateRandomLevel();

        if (current == null || current.getKey().compareTo(key) != 0) {
            if (randomLevel > skipListLevel) {
                for (int i = skipListLevel + 1; i <= randomLevel; i++) {
                    update.set(i, header);
                }
                //更新跳表的层级
                skipListLevel = randomLevel;
            }
            Node<K, V> newNode = createNode(key, value, randomLevel);

            //更新每一层的指针
            for (int i = 0; i <= randomLevel; i++) {
                newNode.forwards.set(i, update.get(i).forwards.get(i));
                update.get(i).forwards.set(i, newNode);
            }
            size++;
            return true;
        }
        return false;
    }

    /**
     * 从跳表中搜索一个键值对
     * @param key 键
     * @return 如果找到则返回 true，否则返回 false
     */
    public boolean search(K key) {
        Node<K, V> current = header;
        for (int i = skipListLevel; i >= 0; i--) {
            while (current.forwards.get(i) != null && current.forwards.get(i).key.compareTo(key) < 0) {
                current = current.forwards.get(i);
            }
        }
        current = current.forwards.get(0);
        return current != null && current.key.compareTo(key) == 0;
    }

    /**
     * 从跳表中获取一个键值对
     * @param key 键
     * @return 如果找到则返回值，否则返回 null
     */
    public V get(K key) {
        Node<K, V> current = header;

        for (int i = skipListLevel; i >= 0; i--) {
            while (current.forwards.get(i) != null && current.forwards.get(i).key.compareTo(key) < 0) {
                current = current.forwards.get(i);
            }
        }

        current = current.forwards.get(0);
        return current != null && current.key.compareTo(key) == 0 ? current.getValue() : null;
    }

    /**
     * 根据键删除一个节点
     * @param key 键
     * @return 如果删除成功则返回 true，否则返回 false
     */
    public synchronized boolean delete(K key) {
        Node<K, V> current = header;
        ArrayList<Node<K, V>> update = new ArrayList<>(Collections.nCopies(MAX_LEVEL + 1, null));

        for (int i = skipListLevel; i >= 0; i--) {
            while (current.forwards.get(i) != null && current.forwards.get(i).key.compareTo(key) < 0) {
                current = current.forwards.get(i);
            }
            update.set(i, current);
        }

        current = current.forwards.get(0);

        if (current != null && current.key.compareTo(key) == 0) {
            for (int i = 0; i < skipListLevel; i++) {
                if (update.get(i).forwards.get(i) != current) {
                    break;
                }
                update.get(i).forwards.set(i, current.forwards.get(i));
            }
            while (skipListLevel > 0 && header.forwards.get(skipListLevel) == null) {
                skipListLevel--;
            }
            size--;
            return true;
        }
        return false;
    }

    /**
     * 持久化跳表
     */
    public void store() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(STORE_PATH))) {
            Node<K, V> current = header.forwards.get(0);
            while (current != null) {
                writer.write(current.getKey() + ":" + current.getValue() + "\n");
                current = current.forwards.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("持久化失败", e);
        }
    }

    /**
     * 从文件中加载跳表
     */
    public void load() {
        try(BufferedReader reader = new BufferedReader(new FileReader(STORE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                Node<K, V> node = parse(line);
                if (node != null) {
                    insert(node.getKey(), node.getValue());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("加载失败", e);
        }
    }
    /**
     * 判断读取的字符串是否合法
     * @param data 读取的字符串
     * @return 如果合法则返回 true，否则返回 false
     */
    private boolean isValid(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        if(data.contains(":")) {
            String[] split = data.split(":");
            return split.length == 2;
        }
        return false;
    }
    /**
     * 根据文件中的持久化字符串，获取 key 和 value，并将 key 和 value 封装到 Node 对象中
     * @param data 文件中的持久化字符串
     */
    @SuppressWarnings("unchecked")
    private Node<K, V> parse(String data) {
        if (!isValid(data)) {
            return null;
        }
        String[] split = data.split(":");
        if (split.length != 2) {
            return null;
        }
        return createNode((K) split[0], (V) split[1], 0);
    }

    /**
     * 打印跳表的结构
     */
    public void displaySkipList() {
        // 从最上层开始向下遍历所有层
        for (int i = this.skipListLevel; i >= 0; i--) {
            Node<K, V> node = this.header.forwards.get(i);
            System.out.print("Level " + i + ": ");
            // 遍历当前层的所有节点
            while (node != null) {
                // 打印当前节点的键和值，键值对之间用":"分隔
                System.out.print(node.getKey() + ":" + node.getValue());
                // 移动到当前层的下一个节点
                node = node.forwards.get(i);
            }
            // 当前层遍历结束，换行
            System.out.println();
        }
    }

    private static class Node<K extends Comparable<K>, V> {
        /**
         * key 键
         */
        private final K key;
        /**
         * level 节点的层级
         */
        int level;
        ArrayList<Node<K, V>> forwards;
        /**
         * value 值
         */
        private V value;

        Node(K key, V value, int level) {
            this.key = key;
            this.value = value;
            this.level = level;
            forwards = new ArrayList<>(Collections.nCopies(level + 1, null));
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        SkipList<String, String> skipList = new SkipList<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine();
            String[] commandList = command.split(" ");
            if (commandList[0].equals("insert")) {
                boolean b = skipList.insert(commandList[1], commandList[2]);
                if (b) {
                    System.out.println("Key: " + commandList[1] + " Value: " + commandList[2] + " insert success!");
                } else {
                    System.out.println("Key: " + commandList[1] + " Value: " + commandList[2] + " insert failed");
                }
            } else if (commandList[0].equals("delete")) {
                boolean b = skipList.delete(commandList[1]);
                if (b) {
                    System.out.println("Key: " + commandList[1] + " deleted!");
                } else {
                    System.out.println("skiplist not exists the key: " + commandList[1]);
                }
            } else if (commandList[0].equals("search")) {
                boolean b = skipList.search(commandList[1]);
                if (b) {
                    System.out.println("Key: " + commandList[1] + " searched!");
                } else {
                    System.out.println("Key: " + commandList[1] + " not exists!");
                }
            } else if (commandList[0].equals("get")) {
                if (!skipList.search(commandList[1])) {
                    System.out.println("Key: " + commandList[1] + " not exists!");
                }
                String node = skipList.get(commandList[1]);
                if (node != null) {
                    System.out.println("Key: " + commandList[1] + "'s value is " + node);
                }
            } else if (commandList[0].equals("dump")) {
                skipList.store();
                System.out.println("Already saved skiplist.");
            } else if (commandList[0].equals("load")) {
                skipList.load();
            } else {
                System.out.println("********skiplist*********");
                skipList.displaySkipList();
                System.out.println("*************************");
            }
        }
    }
}
