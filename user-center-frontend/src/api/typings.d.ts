declare namespace API {
  type BaseResponseBoolean = {
    code?: number
    message?: string
    data?: boolean
  }

  type BaseResponseLong = {
    code?: number
    message?: string
    data?: number
  }

  type BaseResponsePageUserVO = {
    code?: number
    message?: string
    data?: PageUserVO
  }

  type BaseResponseUser = {
    code?: number
    message?: string
    data?: User
  }

  type BaseResponseUserLoginVO = {
    code?: number
    message?: string
    data?: UserLoginVO
  }

  type BaseResponseUserVO = {
    code?: number
    message?: string
    data?: UserVO
  }

  type DeleteRequest = {
    id?: number
  }

  type getUserByIdParams = {
    id: number
  }

  type getUserVOByIdParams = {
    id: number
  }

  type OrderItem = {
    column?: string
    asc?: boolean
  }

  type PageUserVO = {
    records?: UserVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageUserVO
    searchCount?: PageUserVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type User = {
    id?: number
    userAccount?: string
    userPassword?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    phone?: string
    email?: string
    userRole?: string
    vipExpireTime?: string
    vipCode?: string
    vipNumber?: number
    shareCode?: string
    inviteUser?: number
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type UserAddRequest = {
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    phone?: string
    email?: string
    userRole?: string
  }

  type UserLoginRequest = {
    userAccount?: string
    userPassword?: string
  }

  type UserLoginVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    phone?: string
    email?: string
    userRole?: string
    vipExpireTime?: string
    vipCode?: string
    vipNumber?: number
    shareCode?: string
    inviteUser?: number
    editTime?: string
    createTime?: string
    updateTime?: string
  }

  type UserQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    userAccount?: string
    userName?: string
    userProfile?: string
    phone?: string
    email?: string
    userRole?: string
    vipNumber?: number
  }

  type UserRegisterRequest = {
    userAccount?: string
    userPassword?: string
    checkPassword?: string
  }

  type UserUpdateRequest = {
    id?: number
    userName?: string
    userAvatar?: string
    userProfile?: string
    phone?: string
    email?: string
    userRole?: string
  }

  type UserVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    phone?: string
    email?: string
    userRole?: string
    createTime?: string
  }
}
