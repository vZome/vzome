
import { Tabs as KTabs } from "@kobalte/core/tabs";

const Tab = (props) =>
{
  return (
    <KTabs.Content class="tabs__content" value={props.value}>
      {props.children}
    </KTabs.Content>
  )
}

const Tabs = (props) =>
{
  return (
    <KTabs aria-label={props.label} class="tabs" value={props.value} onChange={props.onChange}>
      <KTabs.List class="tabs__list">
        <For each={ props.values }>{ name =>
          <KTabs.Trigger class="tabs__trigger" value={name}>{name}</KTabs.Trigger>
        }</For>
        <KTabs.Indicator class="tabs__indicator" />
      </KTabs.List>
      {props.children}
    </KTabs>
);
}

export { Tabs, Tab }