package edu.buffalo.cse.green.dialogs.wizards;



import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author meep
 * 
 */
public class NewJavaDocPage extends WizardPage {

	private final static String PAGE_NAME = "JavaDocPage";
	private Text _comment;
	private String _text;
	private String _lineSeperator;
	public NewJavaDocPage ( ) {
		super( PAGE_NAME );
		setTitle( NewWizardMessages.NewClassWizardPage_title );
		setDescription( NewWizardMessages.NewClassWizardPage_description );
	}

	/**
	 * Creates the controls for the comment section of the page.
	 * 
	 * @param composite - The parent of the modifier controls group.
	 * 
	 * @author radygert
	 */
	private void createCommentControls ( Composite pageComposite ) {
		Composite commentComposite = new Composite( pageComposite, SWT.NONE );
		commentComposite.setLayout( new GridLayout( 1, false ) );
		commentComposite.setLayoutData( new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
						| GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL ) );
		Label commentLabel = new Label( commentComposite, SWT.LEFT );
		commentLabel.setText( "JavaDoc Comment:" );

		_comment = new Text( commentComposite, SWT.LEFT | SWT.BORDER
				| SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
		_comment.setFont( JFaceResources.getTextFont( ) );
		GridData layoutData = new GridData( GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL );
		_comment.setLayoutData( layoutData );
		_comment.setSelection( 0, _comment.getText( ).length( ) );
		_comment.addModifyListener( new ModifyListener( ) {

			public void modifyText ( ModifyEvent e ) {
				_text = _comment.getText();
				_lineSeperator = _comment.getLineDelimiter();
				NewJavaDocPage.this.setPageComplete( _comment.getCharCount( ) > 5 );
			}

		} );
		setControl( commentComposite );
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl ( Composite parent ) {
		createCommentControls( parent );
		// setErrorMessage("Nope");
		setPageComplete( false );
	}

	public String[] getLines ( ) {
		return _text.split(_lineSeperator);
	}
}
