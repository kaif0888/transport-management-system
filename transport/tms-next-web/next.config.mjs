/** @type {import('next').NextConfig} */
const nextConfig = {
    async rewrites() {
        return [
          {
            source: '/TransportFiles/:path*',
            destination: 'http://localhost/TransportFiles/:path*',
          },
        ]
      },
      transpilePackages: [
    "antd",
    "@ant-design/icons",
    "@ant-design/icons-svg",   // ✅ MISSING ONE (IMPORTANT)
    "rc-util",
    "rc-tooltip",
    "rc-trigger",
    "rc-align",
    "rc-motion",
    "rc-pagination",
    "rc-picker",
    "rc-table",
    "rc-tree",
    "rc-select",
  ],
};



export default nextConfig;
