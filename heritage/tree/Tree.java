package heritage.tree;

import heritage.tree.node.Node;

public class Tree<E> {
    public enum Parent {LEFT, RIGHT}
    
    public Tree() {
        root = new Node<>();
        current = root;
    }
    
    public void insert(E value, Parent direction) {
        if(direction == Parent.LEFT) {
            if(current.getLeft() == null) current.setLeft(new Node<E>());
            current.getLeft().setData(value);
            current.getLeft().setChild(current);
        } else if(direction == Parent.RIGHT) {
            if(current.getRight() == null) current.setRight(new Node<E>()); 
            current.getRight().setData(value);
            current.getRight().setChild(current);
        } else return;
    }
    
    public Node<E> getRoot() {
        return root;
    }
    
    public Node<E> getCurrent() {
        return current;
    }
    
    public void setCurrent(Node<E> child) {
        current = child;
    }
    
    public void setCurrent(Parent direction) {
        if(direction == Parent.LEFT) current = current.getLeft();
        else if(direction == Parent.RIGHT) current = current.getRight();
        else return;
    }
    
    private Node<E> root;
    private Node<E> current; // The current node when traversing the tree.
}
