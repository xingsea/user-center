import { generateService } from '@umijs/openapi';

generateService({
  requestLibPath: "import { request } from './src/request'",
  schemaPath: 'http://localhost:8123/api/v3/api-docs/default',
  serversPath: './src',
})
