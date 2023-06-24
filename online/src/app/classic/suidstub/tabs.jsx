
import Box from '@suid/material/Box'
import FormControl from '@suid/material/FormControl'
import MenuItem from '@suid/material/MenuItem'
import Select from '@suid/material/Select'

const Tab = (props) =>
{
  return <MenuItem value={props.value}>{props.label}</MenuItem>
}

const Tabs = (props) =>
{
  const handleChange = (event) => {
    props.onChange( event, event.target.value );
  };

  return (
    <Box sx={{ minWidth: 270, maxWidth: 400 }}>
      <FormControl fullWidth>
        <Select labelId="tabs-label" id="tabs"
          value={props.value}
          onChange={handleChange}
        >
          {props.children}
        </Select>
      </FormControl>
    </Box>
  );
}

export { Tabs, Tab }