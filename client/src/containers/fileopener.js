
import React,{Component} from 'react'

class FileOpener extends Component { 

	state = { 
    // Initially, no file is selected 
    selectedFile: null
	}; 
	
	// On file select (from the pop up) 
	onFileChange = event => { 
    
    const selected = event.target.files[0]

    if ( !selected )
      return;

    // Update the state 
    this.setState( { selectedFile: selected } )

    console.log( selected )
    
    // read the file
    const reader = new FileReader();

    // file reading finished successfully
    reader.onload = () => {
      var text = reader.result
      const path = "/str/" + this.state.selectedFile.name
      window.cheerpjAddStringFile( path, text )
      console.log( "Loaded " + path )

      window.cjCall( "com.vzome.cheerpj.RemoteClientShim", "openFile", path )
    }

    // file reading failed
    reader.onerror = () => alert('Error : Failed to read file')

    // read as text file
    reader.readAsText( selected )
	}
	
	// File content to be displayed after 
	// file upload is complete 
	fileData = () => { 
    
    if (this.state.selectedFile) { 
      
      return ( 
      <div> 
        <p>File Name: {this.state.selectedFile.name}</p> 
      </div> 
      )
    } else { 
      return ( 
      <div> 
        <br /> 
        <h4>Choose before Pressing the Upload button</h4> 
      </div> 
      )
    } 
	}
	
	render() {
    return ( 
      <div> 
        <h3> 
        Select a vZome file: 
        </h3> 
        <div> 
          <input type="file" onChange={this.onFileChange} accept=".vZome" /> 
        </div> 
      {this.fileData()} 
      </div> 
    ); 
	} 
} 

export default FileOpener; 
