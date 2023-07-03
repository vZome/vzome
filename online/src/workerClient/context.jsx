
import { createContext, useContext } from "solid-js";
import { createWorker } from "./client.js";
import { createWorkerStore } from "./controllers-solid.js";
import { fetchDesign } from "./actions.js";

const WorkerStateContext = createContext( {} );

const WorkerStateProvider = ( props ) =>
{
  const workerClient = props.store || createWorkerStore( createWorker() );
  const { url } = props.config || {};
  url && workerClient.postMessage( fetchDesign( url, props.config ) );

  return (
    <WorkerStateContext.Provider value={workerClient}>
      {props.children}
    </WorkerStateContext.Provider>
  );
}

const useWorkerClient = () => { return useContext( WorkerStateContext ); };

export { WorkerStateProvider, useWorkerClient };