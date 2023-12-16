
import { createContext, createSignal, useContext } from "solid-js";

const createWorker = () =>
{
  // trampolining to work around worker CORS issue
  // see https://github.com/evanw/esbuild/issues/312#issuecomment-1025066671
  const workerPromise = import( "../../worker/vzome-worker-static.js" )
    .then( module => {
      const blob = new Blob( [ `import "${module.WORKER_ENTRY_FILE_URL}";` ], { type: "text/javascript" } );
      const worker = new Worker( URL.createObjectURL( blob ), { type: "module" } );
      worker.onmessage = onWorkerMessage;
      return worker;
    } );
    // TODO: another promise here that resolves only when the worker has responded to an initial message,
    //   then we can get rid of isWorkerReady

  const postMessage = event =>
  {
      workerPromise.then( worker => {
        // console.log( `Message sending to worker: ${JSON.stringify( event, null, 2 )}` );
        worker .postMessage( event );  // send them all, let the worker filter them out
      } )
      .catch( error => {
        console.log( error );
        console.log( "The worker is not available" );
        onWorkerError( 'The worker is not available.  Module workers are supported in the latest versions of most browsers.  Please update your browser.' );
      } );
  }

  const subscribers = [];

  const subscribe = subscriber => subscribers .push( subscriber );

  const subscribeFor = ( type, callback ) =>
  {
    const subscriber = {
      onWorkerError: error => {
        console.log( error );   // TODO: handle the errors in a way the user can see
      },
      onWorkerMessage: data => {
        if ( type === data.type )
          callback( data.payload );
      }
    }
    subscribe( subscriber )
  }

  const onWorkerMessage = message =>
    subscribers .forEach( subscriber => subscriber .onWorkerMessage( message.data ) );
  const onWorkerError = message =>
    subscribers .forEach( subscriber => subscriber .onWorkerError( message ) );

  return { postMessage, subscribe, subscribeFor };
}

const stubContext = {
  isWorkerReady: () => false,
  postMessage:  () => { throw new Error( 'NO WORKER PROVIDER!'); },
  subscribe:    () => { throw new Error( 'NO WORKER PROVIDER!'); },
  subscribeFor: () => { throw new Error( 'NO WORKER PROVIDER!'); },
}

const WorkerStateContext = createContext( stubContext );

const WorkerStateProvider = ( props ) =>
{
  const workerClient = props.workerClient || createWorker();
  const [ isWorkerReady, setReady ] = createSignal( false );

  workerClient .postMessage( { type: 'WINDOW_LOCATION', payload: window.location.toString() } );

  workerClient .subscribeFor( 'CONTROLLER_CREATED', () => setReady( true ) ); // TODO: change to a specific WORKER_READY message
  
  return (
    <WorkerStateContext.Provider value={ { ...workerClient, isWorkerReady } }>
      {props.children}
    </WorkerStateContext.Provider>
  );
}

const useWorkerClient = () => { return useContext( WorkerStateContext ); };

export { createWorker, WorkerStateProvider, useWorkerClient };