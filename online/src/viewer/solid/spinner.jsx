

import Backdrop from '@suid/material/Backdrop';
import CircularProgress from '@suid/material/CircularProgress';

const backdropStyle = {
  backdrop: {
    zIndex: 99,
    position: "absolute",
    color: '#fff',
  },
};

export const Spinner = ( props ) =>
{
  return (
    <Backdrop sx={backdropStyle} open={props.visible}>
      <CircularProgress color="inherit" />
      {/* <Typography variant="h2">{props.message}</Typography> */}
    </Backdrop>
  );
}
