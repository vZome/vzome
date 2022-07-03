
export const fullScreenMode = elem => (
{
  supported: () => {
    return document.fullscreenEnabled;
  },
  on: () => {
    return document.fullscreenElement !== null;
  },
  toggle: () =>
    {
      try {
        if ( !document.fullscreenElement ) {
          if (elem.requestFullscreen) {
            elem.requestFullscreen();
          } else if (elem.webkitRequestFullscreen) { /* Safari */
            elem.webkitRequestFullscreen();
          } else
            console.log( "ERROR: Cannot request fullscreen mode in this browser." );
        } else {
          if (elem.exitFullscreen) {
            elem.exitFullscreen();
          } else if (elem.webkitExitFullscreen) { /* Safari */
            elem.webkitExitFullscreen();
          } else
            console.log( "ERROR: Cannot exit fullscreen mode in this browser." );
        }
      } catch (error) {
        alert( error.message );
      }
    },
});
