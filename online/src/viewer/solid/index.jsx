
const useVZomeUrl = ( url, config={ debug: false } ) =>
{
  const report = useDispatch();
  // TODO: this should be encapsulated in an API on the store
  useEffect( () =>
  {
    if ( !!url ) 
      report( fetchDesign( url, config ) );
  }, [ url ] );
}

export { useVZomeUrl };