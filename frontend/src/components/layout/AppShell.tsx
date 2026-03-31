import { PropsWithChildren } from "react";
import { Header } from "./Header";

export function AppShell({ children }: PropsWithChildren) {
  return (
    <div>
      <Header />
      <main className="page">{children}</main>
    </div>
  );
}
