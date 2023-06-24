
import Alert from '@suid/material/Alert';
import AlertTitle from '@suid/material/AlertTitle';
import Backdrop from '@suid/material/Backdrop';
import { useWorkerClient } from '../../workerClient';

const backdropStyle = {
  backdrop: {
    zIndex: 99,
    position: "absolute",
    color: '#fff',
  },
};

export const ErrorAlert = () =>
{
  const { state, setState } = useWorkerClient();

  const dismissed = () => setState( 'problem', undefined );

  return (
    <Backdrop sx={backdropStyle} open={!!state.problem}
        anchororigin={{ vertical: 'top', horizontal: 'center' }}>
      <Alert variant='filled' severity="error" onClose={dismissed}>
        <AlertTitle>There's a problem</AlertTitle>
        {state.problem}
      </Alert>
    </Backdrop>
  )
}

