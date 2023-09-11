
import Alert from '@suid/material/Alert';
import AlertTitle from '@suid/material/AlertTitle';
import Backdrop from '@suid/material/Backdrop';

import { useWorkerClient } from '../../../workerClient';

export const ErrorAlert = () =>
{
  const { state, setState } = useWorkerClient();

  const dismissed = () => setState( 'problem', undefined );

  return (
    <Backdrop open={!!state.problem} anchororigin={{ vertical: 'top', horizontal: 'center' }}>
      <Alert variant='filled' severity="error" onClose={dismissed}>
      <AlertTitle>There's a problem</AlertTitle>
        {state.problem}
      </Alert>
    </Backdrop>
  );
}