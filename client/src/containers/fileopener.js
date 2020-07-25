
import React,{Component} from 'react'

class FileOpener extends Component { 

	state = { 
    // Initially, no file is selected 
    selectedFile: null
	}; 
	
	// On file select (from the pop up) 
	onFileChange = event => { 
    // Update the state 
    this.setState( { selectedFile: event.target.files[0] } ); 
	}; 
	
	// On file upload (click the upload button) 
	onFileUpload = () => {    
    // Details of the uploaded file 
    console.log(this.state.selectedFile)
    
    // read the file
    const reader = new FileReader();

    // file reading finished successfully
    reader.onload = () => {
      var text = reader.result
      const path = "/str/" + this.state.selectedFile.name
      window.cheerpjAddStringFile( path, text )
      console.log( "Loaded " + path )

      const classpath = "/app/desktop-7.0.jar" +
        ":/app/core-7.0.jar" +
        ":/app/jackson-annotations-2.9.3.jar" +
        ":/app/jackson-core-2.9.5.jar" +
        ":/app/jackson-databind-2.9.5.jar" +
        ":/app/javax.json-1.0.4.jar" +
        ":/app/vecmath-1.6.0-final.jar"

      window.cheerpjRunMain( "org.vorthmann.zome.app.impl.ApplicationController", classpath, path )
    }

    // file reading failed
    reader.onerror = () => alert('Error : Failed to read file')

    // read as text file
    reader.readAsText( this.state.selectedFile )
	}
	
	// File content to be displayed after 
	// file upload is complete 
	fileData = () => { 
    
    if (this.state.selectedFile) { 
      
      return ( 
      <div> 
        <h2>File Details:</h2> 
        <p>File Name: {this.state.selectedFile.name}</p> 
        <p>File Type: {this.state.selectedFile.type}</p> 
        <p> 
        Last Modified:{" "} 
        {this.state.selectedFile.lastModifiedDate.toDateString()} 
        </p> 
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
          <button onClick={this.onFileUpload}> 
          Open 
          </button> 
        </div> 
      {this.fileData()} 
      </div> 
    ); 
	} 
} 

export default FileOpener; 
