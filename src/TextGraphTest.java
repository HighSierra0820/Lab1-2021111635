import static org.junit.jupiter.api.Assertions.*;

class TextGraphTest {
    @org.junit.jupiter.api.Test
    void queryBridgeWords() {
        TextGraph textGraph = new TextGraph();
        textGraph.buildGraph("src/text.txt");
        String result = textGraph.queryBridgeWords("high","sierra");
        assertEquals("No high or sierra in the graph!", result);

        textGraph = new TextGraph();
        textGraph.buildGraph("src/text.txt");
        result = textGraph.queryBridgeWords("day","and");
        assertEquals("No bridge words from day to and!", result);

        textGraph = new TextGraph();
        textGraph.buildGraph("src/text.txt");
        result = textGraph.queryBridgeWords("the","the");
        assertEquals("The bridge words from the to the are: nickname, sun", result);
    }
}