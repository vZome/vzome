
import { createContext, useContext } from "solid-js";
import { createWorker } from "./client.js";
import { createWorkerStore } from "./controllers-solid.js";

const WorkerStateContext = createContext( {} );

const WorkerStateProvider = ( props ) =>
{
  const workerClient = createWorkerStore( createWorker() );
  const { url } = props.config || {};
  url && workerClient.postMessage( fetchDesign( url, config ) );

  return (
    <WorkerStateContext.Provider value={workerClient}>
      {props.children}
    </WorkerStateContext.Provider>
  );
}

const useWorkerClient = () => { return useContext( WorkerStateContext ); };

export { WorkerStateProvider, useWorkerClient };