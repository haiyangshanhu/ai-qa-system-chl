import type { Config } from 'jest';
import nextJest from 'next/jest';

const createJestConfig = nextJest({
  // 提供 Next.js 应用的路径
  dir: './',
});

// Jest 的自定义配置
const config: Config = {
  setupFilesAfterEnv: ['<rootDir>/tests/setup.ts'],
  testEnvironment: 'jest-environment-jsdom',
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/$1',
  },
  collectCoverageFrom: [
    'lib/**/*.{js,jsx,ts,tsx}',
    '!lib/**/*.d.ts',
  ],
  testMatch: [
    '<rootDir>/tests/**/*.{spec,test}.{js,jsx,ts,tsx}',
  ],
};

// createJestConfig is exported in this way to ensure that next/jest can load the Next.js configuration
export default createJestConfig(config);
