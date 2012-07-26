package heritage.tree.node;

public class Node<E> {
    public Node() {
        data = null;
        child = null;
        left = null;
        right = null;
    }
    
    public Node(E data) {
        this.data = data;
        child = null;
        left = null;
        right = null;
    }
    
    public Node(E data, Node<E> child) {
        this.data = data;
        this.child = child;
        left = null;
        right = null;
    }
    
    public E getData() {
        return data;
    }
    
    public Node<E> getChild() {
        return child;
    }
    
    public void setChild(Node<E> child) {
        this.child = child;
    }
    
    public void setData(E data) {
        this.data = data;
    }
    
    public Node<E> getLeft() {
        return left;
    }
    
    public void setLeft(Node<E> left) {
        this.left = left;
    }
    
    public Node<E> getRight() {
        return right;
    }
    
    public void setRight(Node<E> right) {
        this.right = right;
    }
    
    private E data;
    private Node<E> child;
    private Node<E> left;
    private Node<E> right;
}
