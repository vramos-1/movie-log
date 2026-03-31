import { render, RenderOptions } from "@testing-library/react";
import { ReactElement } from "react";
import { MemoryRouter, MemoryRouterProps } from "react-router-dom";

type WrapperOptions = RenderOptions & {
  initialEntries?: MemoryRouterProps["initialEntries"];
};

export function renderWithRouter(
  ui: ReactElement,
  { initialEntries, ...renderOptions }: WrapperOptions = {}
) {
  return render(ui, {
    wrapper: ({ children }) => (
      <MemoryRouter initialEntries={initialEntries}>{children}</MemoryRouter>
    ),
    ...renderOptions
  });
}
