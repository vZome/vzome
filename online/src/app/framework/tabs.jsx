
import { Tabs as KTabs } from "@kobalte/core/tabs";

const Tab = (props) =>
{
  return (
    <KTabs.Content class="tabs__content" value={props.value}>
      <div class="absolute-0">
        <div class="centered-scroller">
          {props.children}
        </div>
      </div>
    </KTabs.Content>
  )
}

const Tabs = (props) =>
{
  return (
    <KTabs aria-label={props.label} class="tabs relative-h100 grid-rows-min-1" value={props.value} onChange={props.onChange}>
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