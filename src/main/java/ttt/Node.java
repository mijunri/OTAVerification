package ttt;


import ota.TimeWords;

public class Node{
    private TimeWords suffix;
    private Node leftChild;
    private Node rightChild;
    private boolean init;
    private boolean accpted;

    public Node(TimeWords suffix){
        this.suffix = suffix;
    }

    public Node(TimeWords suffix, boolean init, boolean accpted){
        this.suffix = suffix;
        this.init = init;
        this.accpted = accpted;
    }

    public TimeWords getSuffix() {
        return suffix;
    }

    public void setSuffix(TimeWords suffix) {
        this.suffix = suffix;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isAccpted() {
        return accpted;
    }

    public void setAccpted(boolean accpted) {
        this.accpted = accpted;
    }

    @Override
    public int hashCode(){
        return suffix.hashCode();
    }

    @Override
    public  boolean equals(Object o){
        Node node = (Node)o;
        return suffix.equals(node.suffix);
    }

    public boolean isLeaf(){
        if(leftChild!=null){
            return false;
        }
        if(rightChild!=null){
            return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return this.suffix.toString();
    }


}
