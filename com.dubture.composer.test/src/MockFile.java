import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

/**
 *
 */
public class MockFile implements IFile
{

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
     */
    @Override
    public void accept(IResourceProxyVisitor visitor, int memberFlags)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor)
     */
    @Override
    public void accept(IResourceVisitor visitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, boolean)
     */
    @Override
    public void accept(IResourceVisitor visitor, int depth,
            boolean includePhantoms) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, int)
     */
    @Override
    public void accept(IResourceVisitor visitor, int depth, int memberFlags)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#clearHistory(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void clearHistory(IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void copy(IPath destination, boolean force, IProgressMonitor monitor)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void copy(IPath destination, int updateFlags,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void copy(IProjectDescription description, boolean force,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void copy(IProjectDescription description, int updateFlags,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#createMarker(java.lang.String)
     */
    @Override
    public IMarker createMarker(String type) throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#createProxy()
     */
    @Override
    public IResourceProxy createProxy()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void delete(boolean force, IProgressMonitor monitor)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#delete(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void delete(int updateFlags, IProgressMonitor monitor)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#deleteMarkers(java.lang.String, boolean, int)
     */
    @Override
    public void deleteMarkers(String type, boolean includeSubtypes, int depth)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#exists()
     */
    @Override
    public boolean exists()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMarker(long)
     */
    @Override
    public IMarker findMarker(long id) throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMarkers(java.lang.String, boolean, int)
     */
    @Override
    public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth)
            throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMaxProblemSeverity(java.lang.String, boolean, int)
     */
    @Override
    public int findMaxProblemSeverity(String type, boolean includeSubtypes,
            int depth) throws CoreException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getFileExtension()
     */
    @Override
    public String getFileExtension()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getLocalTimeStamp()
     */
    @Override
    public long getLocalTimeStamp()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getLocation()
     */
    @Override
    public IPath getLocation()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getLocationURI()
     */
    @Override
    public URI getLocationURI()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getMarker(long)
     */
    @Override
    public IMarker getMarker(long id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getModificationStamp()
     */
    @Override
    public long getModificationStamp()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getPathVariableManager()
     */
    @Override
    public IPathVariableManager getPathVariableManager()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getParent()
     */
    @Override
    public IContainer getParent()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getPersistentProperties()
     */
    @Override
    public Map<QualifiedName, String> getPersistentProperties()
            throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getPersistentProperty(org.eclipse.core.runtime.QualifiedName)
     */
    @Override
    public String getPersistentProperty(QualifiedName key) throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getProject()
     */
    @Override
    public IProject getProject()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getProjectRelativePath()
     */
    @Override
    public IPath getProjectRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getRawLocation()
     */
    @Override
    public IPath getRawLocation()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getRawLocationURI()
     */
    @Override
    public URI getRawLocationURI()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getResourceAttributes()
     */
    @Override
    public ResourceAttributes getResourceAttributes()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getSessionProperties()
     */
    @Override
    public Map<QualifiedName, Object> getSessionProperties()
            throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName)
     */
    @Override
    public Object getSessionProperty(QualifiedName key) throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getType()
     */
    @Override
    public int getType()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getWorkspace()
     */
    @Override
    public IWorkspace getWorkspace()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isAccessible()
     */
    @Override
    public boolean isAccessible()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isDerived()
     */
    @Override
    public boolean isDerived()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isDerived(int)
     */
    @Override
    public boolean isDerived(int options)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isHidden()
     */
    @Override
    public boolean isHidden()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isHidden(int)
     */
    @Override
    public boolean isHidden(int options)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLinked()
     */
    @Override
    public boolean isLinked()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isVirtual()
     */
    @Override
    public boolean isVirtual()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLinked(int)
     */
    @Override
    public boolean isLinked(int options)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLocal(int)
     */
    @Override
    public boolean isLocal(int depth)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isPhantom()
     */
    @Override
    public boolean isPhantom()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isSynchronized(int)
     */
    @Override
    public boolean isSynchronized(int depth)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isTeamPrivateMember()
     */
    @Override
    public boolean isTeamPrivateMember()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isTeamPrivateMember(int)
     */
    @Override
    public boolean isTeamPrivateMember(int options)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void move(IPath destination, boolean force, IProgressMonitor monitor)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void move(IPath destination, int updateFlags,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void move(IProjectDescription description, boolean force,
            boolean keepHistory, IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void move(IProjectDescription description, int updateFlags,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void refreshLocal(int depth, IProgressMonitor monitor)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#revertModificationStamp(long)
     */
    @Override
    public void revertModificationStamp(long value) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setDerived(boolean)
     */
    @Override
    public void setDerived(boolean isDerived) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setDerived(boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setDerived(boolean isDerived, IProgressMonitor monitor)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setHidden(boolean)
     */
    @Override
    public void setHidden(boolean isHidden) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setLocal(boolean, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setLocal(boolean flag, int depth, IProgressMonitor monitor)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setLocalTimeStamp(long)
     */
    @Override
    public long setLocalTimeStamp(long value) throws CoreException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setPersistentProperty(org.eclipse.core.runtime.QualifiedName, java.lang.String)
     */
    @Override
    public void setPersistentProperty(QualifiedName key, String value)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setReadOnly(boolean)
     */
    @Override
    public void setReadOnly(boolean readOnly)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setResourceAttributes(org.eclipse.core.resources.ResourceAttributes)
     */
    @Override
    public void setResourceAttributes(ResourceAttributes attributes)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setSessionProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
     */
    @Override
    public void setSessionProperty(QualifiedName key, Object value)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setTeamPrivateMember(boolean)
     */
    @Override
    public void setTeamPrivateMember(boolean isTeamPrivate)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#touch(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void touch(IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter(Class adapter)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
     */
    @Override
    public boolean contains(ISchedulingRule rule)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
     */
    @Override
    public boolean isConflicting(ISchedulingRule rule)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void appendContents(InputStream source, boolean force,
            boolean keepHistory, IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void appendContents(InputStream source, int updateFlags,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void create(InputStream source, boolean force,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void create(InputStream source, int updateFlags,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#createLink(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void createLink(IPath localLocation, int updateFlags,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#createLink(java.net.URI, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void createLink(URI location, int updateFlags,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void delete(boolean force, boolean keepHistory,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getCharset()
     */
    @Override
    public String getCharset() throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getCharset(boolean)
     */
    @Override
    public String getCharset(boolean checkImplicit) throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getCharsetFor(java.io.Reader)
     */
    @Override
    public String getCharsetFor(Reader reader) throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getContentDescription()
     */
    @Override
    public IContentDescription getContentDescription() throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getContents()
     */
    @Override
    public InputStream getContents() throws CoreException
    {
        InputStream contents = new ByteArrayInputStream("{ }".getBytes());
        return contents;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getContents(boolean)
     */
    @Override
    public InputStream getContents(boolean force) throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getEncoding()
     */
    @Override
    public int getEncoding() throws CoreException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getFullPath()
     */
    @Override
    public IPath getFullPath()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getHistory(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IFileState[] getHistory(IProgressMonitor monitor)
            throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getName()
     */
    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#isReadOnly()
     */
    @Override
    public boolean isReadOnly()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#move(org.eclipse.core.runtime.IPath, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void move(IPath destination, boolean force, boolean keepHistory,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setCharset(java.lang.String)
     */
    @Override
    public void setCharset(String newCharset) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setCharset(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setCharset(String newCharset, IProgressMonitor monitor)
            throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setContents(InputStream source, boolean force,
            boolean keepHistory, IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setContents(IFileState source, boolean force,
            boolean keepHistory, IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setContents(InputStream source, int updateFlags,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setContents(IFileState source, int updateFlags,
            IProgressMonitor monitor) throws CoreException
    {
        // TODO Auto-generated method stub

    }

}
