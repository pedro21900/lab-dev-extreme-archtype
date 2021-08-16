export const HomeRootRouteFragment =  {
  path: 'home',
  loadChildren: () => import('../../view/home/home-page.module').then(m => m.HomePageModule)
}
