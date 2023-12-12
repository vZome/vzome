
import Alert from '@suid/material/Alert';
import AlertTitle from '@suid/material/AlertTitle';
import Backdrop from '@suid/material/Backdrop';

import { useViewer } from '../../../viewer/context/viewer.jsx';

export const ErrorAlert = () =>
{
  const { problem, clearProblem } = useViewer();

  const dismissed = () => clearProblem();

  return (
    <Backdrop open={!!problem()} anchororigin={{ vertical: 'top', horizontal: 'center' }}>
      <Alert variant='filled' severity="error" onClose={dismissed}>
      <AlertTitle>There's a problem</AlertTitle>
        {problem()}
      </Alert>
    </Backdrop>
  );
}