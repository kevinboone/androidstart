/*
 * net.kevinboone.androidstart.StartTreeNodeComparator
 * (c)2014 Kevin Boone
 */
package net.kevinboone.androidstart;

/** Interface that defines a class used to determine whether a StartTreeNode
    meets some criterion. This is used by methods like 
    StartTreeNode.findNodeByComparator() to find a particular node in a list */
public interface StartTreeNodeComparator
{
  public boolean compare (StartTreeNode node);
}


