import request from '../request'
import type { User, PageResult, Result } from '../../types'

export function getUsers(params: { page?: number; size?: number }): Promise<Result<PageResult<User>>> {
  return request.get('/admin/users', { params })
}

export function updateUserStatus(id: number, status: number): Promise<Result<null>> {
  return request.put(`/admin/users/${id}/status`, { status })
}

export function updateUserRole(id: number, role: number): Promise<Result<null>> {
  return request.put(`/admin/users/${id}/role`, { role })
}
