package org.hisp.dhis.ouwt.interceptor;



import java.util.List;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: TreeNode.java 5282 2008-05-28 10:41:06Z larshelg $
 */
public class TreeNode
{
    private long id;

    private String name;

    private boolean selected;

    private boolean hasChildren;

    private List<TreeNode> children;

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public List<TreeNode> getChildren()
    {
        return children;
    }

    public void setChildren( List<TreeNode> children )
    {
        this.children = children;
    }

    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected( boolean selected )
    {
        this.selected = selected;
    }

    public boolean isHasChildren()
    {
        return hasChildren;
    }

    public void setHasChildren( boolean hasChildren )
    {
        this.hasChildren = hasChildren;
    }
}
