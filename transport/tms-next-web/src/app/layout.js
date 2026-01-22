import { Inter } from 'next/font/google'
import "./globals.css";
import dynamic from "next/dynamic";
import { AntdRegistry } from '@ant-design/nextjs-registry';

const Providers = dynamic(() => import("@/provider"), {
  ssr: true,
});

// Use Inter font (compatible with Node 16)
const inter = Inter({
  subsets: ["latin"],
  display: "swap",
  fallback: ["system-ui", "arial", "sans-serif"],
});

export const metadata = {
  title: "Transport Management System",
  description: "TMS - Transport Management System",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={`${inter.className} antialiased`}>
        <Providers>
          <AntdRegistry>
            {children}
          </AntdRegistry>
        </Providers>
      </body>
    </html>
  );
}
