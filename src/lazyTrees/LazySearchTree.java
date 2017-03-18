package lazyTrees;
import cs_1c.Traverser;

import java.util.*;


public class LazySearchTree<E extends Comparable< ? super E > > implements Cloneable
{
    protected int mSize; //Size of undeleted(lazy undelete) nodes
    protected LazySTNode mRoot;
    int mSizeHard; //real size of tree with lazy deleted nodes
    //constructor
    public LazySearchTree() { clear(); }
    public boolean empty() { return (mSize == 0); }
    public int size() { return mSize; }
    public void clear() { mSize = 0; mRoot = null; }
    public int showHeight() { return findHeight(mRoot, -1); }
    private static int noOfDeletions=0;

    public String  sizeHard()
    {
        return("hard size is :"+ mSizeHard);
    }

    public boolean collectGarbage()
    {
        mRoot = collectGarbage(mRoot);
        if(noOfDeletions>0)
        {
            noOfDeletions =0;
            return true;
        }
        else
            return false;
    }

    public E findMin()     //reimplement issue when the last one of the middle one is lolzzzz
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMin(mRoot).data;

    }

    public E findMinHard()
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMinHard(mRoot).data;
    }

    public E findMaxHard()
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMaxHard(mRoot).data;
    }

    public E findMax()
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMax(mRoot).data;
    }

    public E find( E x )
    {
        LazySTNode resultNode;
        resultNode = find(mRoot, x);
        if (resultNode == null)
            throw new NoSuchElementException();
        return resultNode.data;
    }
    public boolean contains(E x)  { return find(mRoot, x) != null; }

    public boolean insert( E x )
    {
        int oldSize = mSize;
        mRoot = insert(mRoot, x);
        return (mSize != oldSize);
    }

    public boolean remove( E x )
    {
        int oldSize = mSize; //
        mRoot = remove(mRoot, x);
        return (mSize != oldSize);
    }

    public < F extends Traverser<? super E > > void traverseHard(F func)
    {
        traverseHard(func, mRoot);
    }

    public < F extends Traverser<? super E >> void traverseSoft(F func)
    {
        traverseSoft(func, mRoot);
    }

    public Object clone() throws CloneNotSupportedException
    {
        LazySearchTree<E> newObject = (LazySearchTree<E>)super.clone();
        newObject.clear();  // can't point to other's data

        newObject.mRoot = cloneSubtree(mRoot);
        newObject.mSize = mSize;

        return newObject;
    }

    // private helper methods ----------------------------------------
    protected LazySTNode findMin(LazySTNode root )
    {
        if (root == null)
            return null;
        if (root.lftChild == null)
        {
            if(root.deleted==false)
                return root;
            if(root.rtChild==null)
                return null;
            else
                return findMin(root.rtChild);
        }
        LazySTNode temp= findMin(root.lftChild);
        if(temp==null)
            return root;
        else
            return temp;
    }

    protected LazySTNode findMinHard(LazySTNode root ) //not lazy
    {
        if (root == null)
            return null;
        if (root.lftChild == null)
            return root;
        return findMinHard(root.lftChild);
    }

    protected LazySTNode findMaxHard(LazySTNode root ) //not lazy
    {
        if (root == null)
            return null;
        if (root.rtChild == null)
            return root;
        return findMaxHard(root.rtChild);
    }

    protected LazySTNode findMax(LazySTNode root ) //lazy
    {
        if (root == null)
            return null;
        if (root.rtChild == null)
        {
            if(root.deleted==false)
                return root;
            else
                return null;
        }
        LazySTNode temp= findMax(root.rtChild);
        if(temp==null)
            return root;
        else
            return temp;
    }

    protected LazySTNode insert(LazySTNode root, E x )
    {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
        {
            mSize++;
            mSizeHard++;
            return new LazySTNode(x, null, null);
        }

        compareResult = x.compareTo(root.data);
        if ( compareResult < 0 )
            root.lftChild = insert(root.lftChild, x);
        else if ( compareResult > 0 )
            root.rtChild = insert(root.rtChild, x);
            //if equal means same exist, so just turn it true
        else if(root.deleted == true)
        {
            root.deleted = false;
            mSize++;
        }
        //Should there be an case of equal and duplicate?
        return root;
    }

    protected LazySTNode removeHard(LazySTNode root, E x)
    {
        int compareResult;  // avoid multiple calls to compareTo()m

        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if ( compareResult < 0 )
            root.lftChild = removeHard(root.lftChild, x);
        else if ( compareResult > 0 )
            root.rtChild = removeHard(root.rtChild, x);

            // found the node
        else if (root.lftChild != null && root.rtChild != null) {
            root.data = findMinHard(root.rtChild).data;
            root.deleted=false;
            root.rtChild = removeHard(root.rtChild, root.data);
        }
        else
        {
            root = (root.lftChild != null)? root.lftChild : root.rtChild;
            mSizeHard--;
        }
        return root;
    }

    protected LazySTNode remove(LazySTNode root, E x  )
    {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if ( compareResult < 0 )
            root.lftChild = remove(root.lftChild, x);
        else if ( compareResult > 0 )
            root.rtChild = remove(root.rtChild, x);

            // found the node for lazy deletion
        else
        {
            root.deleted = true;
            mSize--;
        }
        return root;
        // found the node
        /*else if (root.lftChild != null && root.rtChild != null)
        {
            root.data = findMin(root.rtChild).data;
            root.rtChild = remove(root.rtChild, root.data);
        }
        else
        {
            root = (root.lftChild != null)? root.lftChild : root.rtChild;
            mSize--;
        }
        */

    }

    LazySTNode collectGarbage(LazySTNode treeNode)
    {


        if(treeNode == null)
            return null;
        //System.out.print("Having an entry in"+treeNode.data);
        if(treeNode.lftChild!=null)
            treeNode.lftChild=collectGarbage(treeNode.lftChild);
        if(treeNode.rtChild!=null)
            treeNode.rtChild=collectGarbage(treeNode.rtChild);
        if(treeNode.deleted==true)
        {
            treeNode = removeHard(treeNode,treeNode.data);
            noOfDeletions++;
        }
        return treeNode;
    }

    protected <F extends Traverser<? super E>> void traverseHard(F func, LazySTNode treeNode)
    {
        if (treeNode == null)
            return;

        traverseHard(func, treeNode.lftChild);
        func.visit(treeNode.data);
        traverseHard(func, treeNode.rtChild);
    }

    protected <F extends Traverser<? super E>> void traverseSoft(F func, LazySTNode treeNode)
    {
        if (treeNode == null)
            return;
        traverseSoft(func, treeNode.lftChild);
        if(treeNode.deleted==false)
            func.visit(treeNode.data);
        traverseSoft(func, treeNode.rtChild);
    }

    protected LazySTNode find(LazySTNode root, E x )
    {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if (compareResult < 0)
            return find(root.lftChild, x);
        if (compareResult > 0)
            return find(root.rtChild, x);
        if(root.deleted==false)
            return root;   // found
        else
            return null;   //not found as deleted

    }

    protected LazySTNode cloneSubtree(LazySTNode root)
    {
        LazySTNode newNode;
        if (root == null)
            return null;

        // does not set myRoot which must be done by caller
        newNode = new LazySTNode
                (
                        root.data,
                        cloneSubtree(root.lftChild),
                        cloneSubtree(root.rtChild)
                );
        return newNode;
    }

    protected int findHeight(LazySTNode treeNode, int height )
    {
        int leftHeight, rightHeight;
        if (treeNode == null)
            return height;
        height++;
        leftHeight = findHeight(treeNode.lftChild, height);
        rightHeight = findHeight(treeNode.rtChild, height);
        return (leftHeight > rightHeight)? leftHeight : rightHeight;
    }
    public class LazySTNode
    {
        public boolean deleted=false;
        // use public access so the tree or other classes can access members
        public LazySTNode lftChild, rtChild;
        public E data;
        public LazySTNode myRoot;  // needed to test for certain error


        public LazySTNode(E d, LazySTNode lft, LazySTNode rt )
        {
            lftChild = lft;
            rtChild = rt;
            data = d;
        }

        public LazySTNode()
        {
            this(null, null, null);
        }

        // function stubs -- for use only with AVL Trees when we extend
        public int getHeight() { return 0; }
        boolean setHeight(int height) { return true; }
    }


}